package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.subresource;

import javax.ws.rs.container.ResourceContext;
import java.util.Collections;
import java.util.Set;

public class TestClass2 {

    public Object method() {
        ResourceContext rc = null;
        return rc.getResource(SubResource.class);
    }

    public static Set<String> getResult() {
        return Collections.singleton("com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.subresource.TestClass2$SubResource");
    }

    private class SubResource {
    }

}
