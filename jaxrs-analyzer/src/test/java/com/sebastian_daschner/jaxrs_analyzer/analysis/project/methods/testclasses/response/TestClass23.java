package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class TestClass23 {

    // continue testing all available non-static Response methods
    public Response method() {
        Response.ResponseBuilder responseBuilder = Response.ok();
        responseBuilder.entity(12L, new Annotation[0]);
        responseBuilder.link("", "rel");
        responseBuilder.status(Response.Status.EXPECTATION_FAILED);
        responseBuilder.tag("");
        responseBuilder.type(MediaType.APPLICATION_XML);
        responseBuilder.variants();

        return responseBuilder.build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().addAll(Arrays.asList(200, 417));
        result.getEntityTypes().addAll(Arrays.asList("java.lang.Long"));
        result.getHeaders().addAll(Arrays.asList("Link", "ETag", "Vary"));
        result.getContentTypes().add("application/xml");

        return Collections.singleton(result);
    }

}
