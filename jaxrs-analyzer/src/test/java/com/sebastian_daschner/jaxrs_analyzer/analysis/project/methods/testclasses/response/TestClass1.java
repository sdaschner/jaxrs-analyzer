package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class TestClass1 {

    public Response method() {
        int status = 200;
        int anotherStatus = 100;
        status = anotherStatus = 300;
        return Response.status(anotherStatus).entity(status).build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getEntityTypes().add("int");
        result.getStatuses().addAll(Arrays.asList(100, 300));

        return Collections.singleton(result);
    }

}
