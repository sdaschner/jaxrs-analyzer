package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.subresource;

import javax.ws.rs.container.ResourceContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestClass3 {

    public Object method() {
        ResourceContext rc = null;
        final Object resource = rc.getResource(SubResource.class);
        return resource;
    }

    public static Set<String> getResult() {
        return new HashSet<>(Arrays.asList("com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.subresource.TestClass3$SubResource",
                "java.lang.Object"));
    }

    private class SubResource {
    }

}
