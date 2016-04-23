package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.testclasses.resource.object;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import java.util.Collections;
import java.util.Set;

public class TestClass2 {

    @javax.ws.rs.GET
    public String method() {
        if ("".equals(""))
            return "Hi World!";
        return "Hello World!";
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();
        result.getEntityTypes().add(Types.STRING);

        return Collections.singleton(result);
    }

}
