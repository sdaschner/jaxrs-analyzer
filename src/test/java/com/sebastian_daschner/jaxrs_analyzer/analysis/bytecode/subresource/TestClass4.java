package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.subresource;

import javax.ws.rs.container.ResourceContext;
import java.util.Collections;
import java.util.Set;

public class TestClass4 {

    private ResourceContext rc;

    public Object method() {
        return rc.initResource(new SubResource());
    }

    public static Set<String> getResult() {
        return Collections.singleton("com/sebastian_daschner/jaxrs_analyzer/analysis/bytecode/subresource/TestClass4$SubResource");
    }

    private class SubResource {
    }

}
