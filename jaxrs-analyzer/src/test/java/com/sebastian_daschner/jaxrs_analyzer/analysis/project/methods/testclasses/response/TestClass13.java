package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;

public class TestClass13 {

    public Response method(final String id) {
        final int status = getStatus();

        if ("".equals(""))
            throw new IllegalArgumentException(String.valueOf(status));

        return Response.status(status).build();
    }

    private int getStatus() {
        return 201;
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().add(201);

        return Collections.singleton(result);
    }

}
