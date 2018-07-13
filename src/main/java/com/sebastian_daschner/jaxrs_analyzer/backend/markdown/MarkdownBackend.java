package com.sebastian_daschner.jaxrs_analyzer.backend.markdown;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.sebastian_daschner.jaxrs_analyzer.backend.StringBackend;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.MethodParameter;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.ParameterType;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.ResourceMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Response;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.utils.StringUtils;

import static com.sebastian_daschner.jaxrs_analyzer.backend.ComparatorUtils.mapKeyComparator;
import static com.sebastian_daschner.jaxrs_analyzer.backend.ComparatorUtils.parameterComparator;
import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.toReadableType;


/**
 * A backend implementation which produces an Markdown representation of the JAX-RS project.
 *
 * @author Sven Ehnert
 */
public class MarkdownBackend extends StringBackend {

    private static final String NAME = "Markdown";
    private static final String DOCUMENT_TITLE = "# REST resources of ";
    private static final String TYPE_WILDCARD = "\\*/*";

    @Override
    protected void appendMethod(final String baseUri, final String resource, final ResourceMethod resourceMethod) {
        builder.append("## `").append(resourceMethod.getMethod()).append(' ');
        if (!StringUtils.isBlank(baseUri))
            builder.append(baseUri).append('/');
        builder.append(resource).append("`\n\n");
        if( !StringUtils.isBlank( resourceMethod.getDescription() ) )
            builder.append( "### Description: " ).append( resourceMethod.getDescription() ).append( "\n\n" );
        if (resourceMethod.isDeprecated())
            builder.append("CAUTION: deprecated\n\n");
    }

    @Override
    protected void appendRequest(final ResourceMethod resourceMethod) {
        builder.append("### Request\n");

        if (resourceMethod.getRequestBody() != null) {
            builder.append("*Content-Type*: `");
            builder.append(resourceMethod.getRequestMediaTypes().isEmpty() ? TYPE_WILDCARD : toString(resourceMethod.getRequestMediaTypes()));
            builder.append("` + \n");

            builder.append("*Request Body*: (").append(toTypeOrCollection(resourceMethod.getRequestBody())).append(")");
            Optional.ofNullable(resources.getTypeRepresentations().get(resourceMethod.getRequestBody())).ifPresent(
                    this::generateSample);
            builder.append("\n");
        } else {
            builder.append("_No body_ + \n");
        }

        final Set<MethodParameter> parameters = resourceMethod.getMethodParameters();

        appendParams("Path Param", parameters, ParameterType.PATH);
        appendParams("Query Param", parameters, ParameterType.QUERY);
        appendParams("Form Param", parameters, ParameterType.FORM);
        appendParams("Header Param", parameters, ParameterType.HEADER);
        appendParams("Cookie Param", parameters, ParameterType.COOKIE);
        appendParams("Matrix Param", parameters, ParameterType.MATRIX);

        builder.append('\n');
    }

    private void appendParams(final String name, final Set<MethodParameter> parameters, final ParameterType parameterType) {
        parameters.stream().filter(p -> p.getParameterType() == parameterType)
                .sorted(parameterComparator()).forEach(p -> builder
                .append('*')
                .append(name)
                .append("*: `")
                .append(p.getName())
                .append("`, `")
                .append(toReadableType(p.getType().getType()))
                // TODO add default value
                .append("` + \n"));
    }

    @Override
    protected void appendResponse(final ResourceMethod resourceMethod) {
        builder.append("### Response\n");

        builder.append("*Content-Type*: `");
        builder.append(resourceMethod.getResponseMediaTypes().isEmpty() ? TYPE_WILDCARD : toString(resourceMethod.getResponseMediaTypes()));
        builder.append("`\n\n");

        resourceMethod.getResponses().entrySet().stream().sorted(mapKeyComparator()).forEach(e -> {
            builder.append("#### `").append(e.getKey()).append(' ')
                    .append(javax.ws.rs.core.Response.Status.fromStatusCode(e.getKey()).getReasonPhrase()).append("`\n");
            final Response response = e.getValue();
            response.getHeaders().forEach(h -> builder.append("*Header*: `").append(h).append("` + \n"));

            if (response.getResponseBody() != null) {
                builder.append("*Response Body*: ").append('(').append(toTypeOrCollection(response.getResponseBody())).append(")");
                Optional.ofNullable(resources.getTypeRepresentations().get(response.getResponseBody())).ifPresent(
                        this::generateSample);
                builder.append("\n");
            }

            builder.append('\n');
        });
    }

    private void generateSample(TypeRepresentation r) {
        builder.append("\n\n```javascript\n");
        builder.append(doVisit(r));
        builder.append("\n```\n\n");
    }

    private String toTypeOrCollection(final TypeIdentifier type) {
        final TypeRepresentation representation = resources.getTypeRepresentations().get(type);
        if (representation != null && !representation.getComponentType().equals(type) && !type.getType().equals(Types.JSON)) {
            return "Collection of `" + toReadableComponentType(representation.getComponentType()) + '`';
        }
        return '`' + toReadableType(type.getType()) + '`';
    }

    private static String toString(final Set<String> set) {
        return set.stream().sorted().map(Object::toString).collect(Collectors.joining(", "));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void appendFirstLine() {
        builder.append(DOCUMENT_TITLE).append(projectName).append("\n\n");
    }

}
