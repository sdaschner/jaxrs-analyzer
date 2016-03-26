package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.resource.object;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import java.util.*;

public class TestClass6 {

    public List<Model> method() {
        if ("".equals(""))
            return new ArrayList<>();
        return new LinkedList<>();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();
        result.getEntityTypes().add("java.util.List<com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.resource.object.TestClass6$Model>");

        return Collections.singleton(result);
    }

    private class Model {
        public Model(final String string) {
        }
    }

}
