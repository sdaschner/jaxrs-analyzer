package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.testclasses.resource.object;

import com.sebastian_daschner.jaxrs_analyzer.builder.HttpResponseBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;

import java.util.Collections;
import java.util.Set;

public class TestClass13 {

    private TestManager<String> manager;

    @javax.ws.rs.GET public String method() {
        final String test = manager.getTest();
        System.out.println(test.length());
        return test;
    }

    public static Set<HttpResponse> getResult() {
        return Collections.singleton(HttpResponseBuilder.newBuilder().andEntityTypes(Types.STRING).build());
    }

    private static class TestManager<T> {

        private T object;

        public T getTest() {
//            final T object = this.object;
            System.out.println(object);
            return object;
        }
    }

}
