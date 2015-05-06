package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.subresource;

import javax.ws.rs.container.ResourceContext;

public class TestClass3 {

    public Object method() {
        ResourceContext rc = null;
        final Object resource = rc.getResource(SubResource.class);
        return resource;
    }

    public static String getResult() {
        // TODO uncomment
//        return "com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.subresource.TestClass3$SubResource";
        return "java.lang.Object";
    }

    private class SubResource {
    }

}
