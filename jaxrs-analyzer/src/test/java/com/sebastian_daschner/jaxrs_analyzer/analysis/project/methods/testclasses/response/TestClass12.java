package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class TestClass12 {

    public Response method(final String id) {
        final int status = getStatus();
        return Response.status(status).build();
    }

    private int getStatus() {
        if ("".equals(""))
            return 200;
        return 201;
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().addAll(Arrays.asList(200, 201));

        return Collections.singleton(result);
    }

}
