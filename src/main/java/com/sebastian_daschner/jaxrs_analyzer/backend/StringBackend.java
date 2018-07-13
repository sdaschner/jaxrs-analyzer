package com.sebastian_daschner.jaxrs_analyzer.backend;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.*;

import javax.json.*;
import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.toReadableType;
import static java.util.Collections.singletonMap;
import static java.util.Comparator.comparing;

/**
 * A backend that is backed by Strings (plain text).
 *
 * @author Sebastian Daschner
 */
public abstract class StringBackend implements Backend {

    public static final String INLINE_PRETTIFY = "inlinePrettify";
    private static final String INLINE_PRETTIFY_DEFAULT = "true";
    private static final Pattern CLEAR_COMPONENT_TYPE = Pattern.compile("[+\\-]");

    protected final Lock lock = new ReentrantLock();
    protected StringBuilder builder;
    protected Resources resources;
    protected String projectName;
    protected String projectVersion;
    protected boolean prettify;

    @Override
    public void configure(final Map<String, String> config) {
        prettify = Boolean.parseBoolean(config.getOrDefault(INLINE_PRETTIFY, INLINE_PRETTIFY_DEFAULT));
    }

    @Override
    public byte[] render(final Project project) {
        lock.lock();
        try {
            initRender(project);

            final String output = renderInternal();

            return serialize(output);
        } finally {
            lock.unlock();
        }
    }

    private void initRender(final Project project) {
        // initialize fields
        builder = new StringBuilder();
        resources = project.getResources();
        projectName = project.getName();
        projectVersion = project.getVersion();
    }

    private String renderInternal() {
        appendHeader();

        resources.getResources().stream().sorted().forEach(this::appendResource);

        return builder.toString();
    }

    private void appendHeader() {
        appendFirstLine();
        builder.append(projectVersion).append("\n\n");
    }

    private void appendResource(final String resource) {
        resources.getMethods(resource).stream()
                .sorted(comparing(ResourceMethod::getMethod))
                .forEach(resourceMethod -> {
                    appendMethod(resources.getBasePath(), resource, resourceMethod);
                    appendRequest(resourceMethod);
                    appendResponse(resourceMethod);
                    appendResourceEnd();
                });
    }

    protected abstract void appendFirstLine();

    protected abstract void appendMethod(String baseUri, String resource, ResourceMethod resourceMethod);

    protected abstract void appendRequest(ResourceMethod resourceMethod);

    protected abstract void appendResponse(ResourceMethod resourceMethod);

    protected void appendResourceEnd() {
    }

    protected String doVisit(final TypeRepresentation typeRepresentation) {
        final StringBuilder builder = new StringBuilder();
        final TypeRepresentationVisitor appender = new JsonRepresentationAppender(builder,
                resources.getTypeRepresentations());
        typeRepresentation.accept(appender);
        final String json = builder.toString();
        return prettify ? format(json) : json;
    }

    protected String toReadableComponentType(TypeIdentifier componentType) {
        final String type = componentType.getType();
        return toReadableType(CLEAR_COMPONENT_TYPE.matcher(type).replaceAll(""));
    }

    private static byte[] serialize(final String output) {
        return output.getBytes();
    }

    private String format(final String json) {
        final JsonProvider provider = JsonProvider.provider();
        final StringWriter out = new StringWriter();
        try (final JsonReader reader = provider.createReader(new StringReader(json));
             final JsonWriter jsonWriter = provider.createWriterFactory(singletonMap(JsonGenerator.PRETTY_PRINTING, true))
                     .createWriter(out)) {

            // jsonWriter.write(reader.readValue()); // bug in RI, can switch to johnzon
            final JsonStructure read = reader.read();
            if (read.getValueType() == JsonValue.ValueType.OBJECT) {
                jsonWriter.writeObject(JsonObject.class.cast(read));
            } else if (read.getValueType() == JsonValue.ValueType.ARRAY) {
                jsonWriter.writeArray(JsonArray.class.cast(read));
            } else { // no reformatting
                return json;
            }
            return out.toString().trim();
        }
    }
}
