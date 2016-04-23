package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.subresource;

import javax.ws.rs.container.ResourceContext;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singletonList;

public class TestClass3 {

    public Object method() {
        ResourceContext rc = null;
        final Object resource = rc.getResource(SubResource.class);
        return resource;
    }

    public static Set<String> getResult() {
        return new HashSet<>(singletonList("com/sebastian_daschner/jaxrs_analyzer/analysis/bytecode/subresource/TestClass3$SubResource"));
    }

    private class SubResource {
    }

}
