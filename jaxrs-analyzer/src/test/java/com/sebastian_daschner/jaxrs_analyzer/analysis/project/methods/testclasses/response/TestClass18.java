package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.util.*;

public class TestClass18 {

    // test not particularly useful; only testing generic map support
    public Response method() {
        final Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        GenericEntity<Map<String, String>> genericEntity = new GenericEntity<>(map, Map.class);
        return Response.ok(genericEntity).build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().addAll(Arrays.asList(200));
        result.getEntityTypes().addAll(Arrays.asList("javax.ws.rs.core.GenericEntity<java.util.Map<java.lang.String, java.lang.String>>"));

        return Collections.singleton(result);
    }

}
