package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.resource.object;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TestClass5 {

    public List<Model> method() {
        if ("".equals(""))
            return Arrays.asList(new Model("hi"));
        return Collections.singletonList(new Model("Hello World!"));
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();
        result.getEntityTypes().add("java.util.List<com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.resource.object.TestClass5$Model>");

        return Collections.singleton(result);
    }

    private class Model {
        public Model(final String string) {
        }
    }

}
