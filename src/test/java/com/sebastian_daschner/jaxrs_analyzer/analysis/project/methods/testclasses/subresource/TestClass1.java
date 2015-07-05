package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.subresource;

import javax.ws.rs.container.ResourceContext;
import java.util.Collections;
import java.util.Set;

public class TestClass1 {

    public SubResource method() {
        ResourceContext rc = null;
        return rc.getResource(SubResource.class);
    }

    public static Set<String> getResult() {
        return Collections.singleton("com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.subresource.TestClass1$SubResource");
    }

    private class SubResource {
    }
}
