package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.testclasses.resource.object;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import java.util.Collections;
import java.util.Set;

public class TestClass9 {

    @javax.ws.rs.GET public void method() {
        if ("".equals(""))
            System.out.println("do something");
    }

    public static Set<HttpResponse> getResult() {
        return Collections.emptySet();
    }

}
