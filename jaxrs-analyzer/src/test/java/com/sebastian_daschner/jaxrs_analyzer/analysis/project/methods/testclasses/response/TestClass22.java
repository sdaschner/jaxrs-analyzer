package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.*;

public class TestClass22 {

    // test all available non-static Response methods
    public Response method() {
        Response.ResponseBuilder responseBuilder = Response.ok();
        responseBuilder.header("X-Test", "Hello");
        responseBuilder.cacheControl(CacheControl.valueOf(""));
        responseBuilder.contentLocation(URI.create(""));
        responseBuilder.cookie();
        responseBuilder.entity(12d);
        responseBuilder.expires(new Date());
        responseBuilder.language(Locale.ENGLISH);
        responseBuilder.encoding("UTF-8");
        responseBuilder.lastModified(new Date());
        responseBuilder.link(URI.create(""), "rel");
        responseBuilder.location(URI.create(""));
        responseBuilder.status(433);
        responseBuilder.tag(new EntityTag(""));
        responseBuilder.type(MediaType.APPLICATION_JSON_TYPE);
        responseBuilder.variants(new LinkedList<>());

        return responseBuilder.build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().addAll(Arrays.asList(200, 433));
        result.getEntityTypes().addAll(Arrays.asList("java.lang.Double"));
        result.getHeaders().addAll(Arrays.asList("X-Test", "Cache-Control", "Set-Cookie", "Expires", "Content-Language", "Content-Encoding",
                "Last-Modified", "Link", "Location", "ETag", "Vary", "Content-Location"));
        result.getContentTypes().add("application/json");

        return Collections.singleton(result);
    }

}
