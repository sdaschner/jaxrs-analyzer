package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestClass20 {

    // continue testing all available static Response methods
    public Response method() {
        Response.ResponseBuilder responseBuilder = Response.accepted("Hello");
        responseBuilder = Response.notModified(new EntityTag(""));
        responseBuilder = Response.ok(1, MediaType.APPLICATION_XML);

        return responseBuilder.build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse firstResult = new HttpResponse();
        final HttpResponse secondResult = new HttpResponse();
        final HttpResponse thirdResult = new HttpResponse();

        firstResult.getStatuses().add(202);
        firstResult.getEntityTypes().add("java.lang.String");

        secondResult.getStatuses().add(304);
        secondResult.getHeaders().add("ETag");

        thirdResult.getStatuses().add(200);
        thirdResult.getContentTypes().add("application/xml");
        thirdResult.getEntityTypes().add("int");

        return new HashSet<>(Arrays.asList(firstResult, secondResult, thirdResult));
    }

}
