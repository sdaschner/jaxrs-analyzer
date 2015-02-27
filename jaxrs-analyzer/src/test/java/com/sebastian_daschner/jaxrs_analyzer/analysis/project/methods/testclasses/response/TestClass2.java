package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class TestClass2 {

    public Response method() {
        int status = 200;
        if ((status = 300) > 0) {
            status = 100;
        }
        return Response.status(status).build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().addAll(Arrays.asList(100, 200, 300));

        return Collections.singleton(result);
    }

}
