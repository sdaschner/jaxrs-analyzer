package com.sebastian_daschner.jaxrs_analyzer.builder;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.ResourceMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;

import java.util.stream.Stream;

/**
 * @author Sebastian Daschner
 */
public class ResourcesBuilder {

    private final Resources resources;

    private ResourcesBuilder() {
        resources = new Resources();
    }

    public static ResourcesBuilder withBase(final String baseUri) {
        final ResourcesBuilder builder = new ResourcesBuilder();
        builder.resources.setBasePath(baseUri);
        return builder;
    }

    public ResourcesBuilder andResource(final String resource, final ResourceMethod... method) {
        Stream.of(method).forEach(m -> resources.addMethod(resource, m));
        return this;
    }

    public Resources build() {
        return resources;
    }

}
