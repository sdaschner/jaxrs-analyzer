package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.util.*;

public class TestClass11 {

    public Response method() {
        List<String> strings = new LinkedList<>();
        strings.add("test");
        GenericEntity<List<String>> genericEntity = new GenericEntity<>(strings, List.class);
        return Response.ok(genericEntity).build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().addAll(Arrays.asList(200));
        result.getEntityTypes().addAll(Arrays.asList("javax.ws.rs.core.GenericEntity<java.util.List<java.lang.String>>"));

        return Collections.singleton(result);
    }

}
