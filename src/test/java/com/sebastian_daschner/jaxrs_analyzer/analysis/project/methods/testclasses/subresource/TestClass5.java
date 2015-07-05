package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.subresource;

import javax.ws.rs.container.ResourceContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestClass5 {

    public Object method() {
        ResourceContext rc = null;
        if ("".equals(""))
            return rc.getResource(AnotherSubResource.class);
        return rc.getResource(SubResource.class);
    }

    public static Set<String> getResult() {
        return new HashSet<>(Arrays.asList("com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.subresource.TestClass5$SubResource",
                "com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.subresource.TestClass5$AnotherSubResource"));
    }

    private class SubResource {
    }

    private class AnotherSubResource {
    }

}
