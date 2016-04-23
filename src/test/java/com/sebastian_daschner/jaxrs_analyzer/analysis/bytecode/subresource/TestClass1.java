package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.subresource;

import javax.ws.rs.container.ResourceContext;
import java.util.Set;

import static java.util.Collections.singleton;

public class TestClass1 {

    public SubResource method() {
        ResourceContext rc = null;
        return rc.getResource(SubResource.class);
    }

    public static Set<String> getResult() {
        return singleton("com/sebastian_daschner/jaxrs_analyzer/analysis/bytecode/subresource/TestClass1$SubResource");
    }

    private class SubResource {
    }
}
