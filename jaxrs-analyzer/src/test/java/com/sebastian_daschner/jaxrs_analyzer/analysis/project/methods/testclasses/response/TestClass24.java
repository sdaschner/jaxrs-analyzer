package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

public class TestClass24 {

    // continue testing all available non-static Response methods
    public Response method() {
        Response.ResponseBuilder responseBuilder = Response.ok();
        responseBuilder.links(new Link[0]);
        responseBuilder.variant(new Variant(MediaType.TEXT_PLAIN_TYPE, Locale.ENGLISH, "UTF-8"));

        return responseBuilder.build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().addAll(Arrays.asList(200));
        result.getHeaders().addAll(Arrays.asList("Link", "Content-Language", "Content-Encoding"));

        return Collections.singleton(result);
    }

}
