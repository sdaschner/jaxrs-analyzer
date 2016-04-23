package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.subresource;

import javax.ws.rs.container.ResourceContext;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singleton;

public class TestClass5 {

    public Object method() {
        ResourceContext rc = null;
        if ("".equals(""))
            return rc.getResource(AnotherSubResource.class);
        return rc.getResource(SubResource.class);
    }

    public static Set<String> getResult() {
        // FEATURE test several resources
//        return new HashSet<>(Arrays.asList("com/sebastian_daschner/jaxrs_analyzer/analysis/bytecode/subresource/TestClass5$SubResource",
//                "com/sebastian_daschner/jaxrs_analyzer/analysis/bytecode/subresource/TestClass5$AnotherSubResource"));
        return new HashSet<>(singleton("com/sebastian_daschner/jaxrs_analyzer/analysis/bytecode/subresource/TestClass5$SubResource"));
    }

    private class SubResource {
    }

    private class AnotherSubResource {
    }

}
