package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.testclasses.resource.object;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TestClass3 {

    @javax.ws.rs.GET public List<String> method() {
        if ("".equals(""))
            return Arrays.asList("Hi World!");
        return Collections.singletonList("Hello World!");
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();
        result.getEntityTypes().add("Ljava/util/List<Ljava/lang/String;>;");

        return Collections.singleton(result);
    }

}
