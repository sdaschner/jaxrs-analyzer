package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.subresource;

import javax.ws.rs.container.ResourceContext;

public class TestClass2 {

    public Object method() {
        ResourceContext rc = null;
        return rc.getResource(SubResource.class);
    }

    public static String getResult() {
        return "com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.subresource.TestClass2$SubResource";
    }

    private class SubResource {
    }

}
