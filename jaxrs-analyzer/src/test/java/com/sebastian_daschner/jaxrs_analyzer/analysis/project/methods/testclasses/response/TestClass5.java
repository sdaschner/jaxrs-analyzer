package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestClass5 {

    private EntityManager entityManager;

    public Response method() {

        final TestClass5 testClass = entityManager.find(TestClass5.class, 1);

        if (testClass.method().hasEntity())
            return buildWithStatus(Response.Status.ACCEPTED);

        return buildWithStatus(Response.Status.CONFLICT);
    }

    private static Response buildWithStatus(final Response.Status status) {
        return Response.status(status).header("X-Message", "Secret message").build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse firstResult = new HttpResponse();
        final HttpResponse secondResult = new HttpResponse();

        firstResult.getStatuses().add(202);
        firstResult.getHeaders().add("X-Message");


        secondResult.getStatuses().add(409);
        secondResult.getHeaders().add("X-Message");

        return new HashSet<>(Arrays.asList(firstResult, secondResult));
    }

}
