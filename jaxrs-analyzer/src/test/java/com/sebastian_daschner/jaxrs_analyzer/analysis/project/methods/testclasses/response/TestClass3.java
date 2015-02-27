package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;

public class TestClass3 {

    public Response method() {
        int status = 200;
        if ((status = 300) > 0) {
            Response.status(status).build();
        }
        status = 100;
        return Response.status(200).entity(status).build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getEntityTypes().add("int");
        result.getStatuses().add(200);

        return Collections.singleton(result);
    }

}
