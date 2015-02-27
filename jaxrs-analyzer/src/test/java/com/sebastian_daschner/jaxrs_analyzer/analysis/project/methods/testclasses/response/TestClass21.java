package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class TestClass21 {

    // continue testing all available static Response methods
    public Response method() {
        Response.ResponseBuilder responseBuilder = Response.notModified("");
        responseBuilder = Response.ok(1d, MediaType.APPLICATION_JSON_TYPE);
        responseBuilder = Response.ok(1L, new Variant(MediaType.TEXT_PLAIN_TYPE, Locale.ENGLISH, "UTF-8"));

        return responseBuilder.build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse firstResult = new HttpResponse();
        final HttpResponse secondResult = new HttpResponse();
        final HttpResponse thirdResult = new HttpResponse();

        firstResult.getStatuses().add(304);
        firstResult.getHeaders().add("ETag");

        secondResult.getStatuses().add(200);
        secondResult.getContentTypes().add("application/json");
        secondResult.getEntityTypes().add("java.lang.Double");

        thirdResult.getStatuses().add(200);
        thirdResult.getHeaders().addAll(Arrays.asList("Content-Language", "Content-Encoding"));
        thirdResult.getEntityTypes().add("java.lang.Long");

        return new HashSet<>(Arrays.asList(firstResult, secondResult, thirdResult));
    }

}
