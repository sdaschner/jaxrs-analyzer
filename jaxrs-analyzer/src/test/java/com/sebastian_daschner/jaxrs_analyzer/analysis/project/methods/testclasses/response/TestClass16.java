package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;

/**
 * @author Sebastian Daschner
 */

public class TestClass16 {

    public TestClass16(final String test) {
        System.out.println(test);
    }

    public Response method(final String id) {
        final Response.Status status = Response.Status.OK;
        new TestClass16(status.getReasonPhrase());
        return Response.status(status).header("X-Header", "Hello World").build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().add(200);
        result.getHeaders().add("X-Header");

        return Collections.singleton(result);
    }

}
