package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class TestClass17 {

    // test not particularly useful; only testing array type support
    public Response method() {
        String[] strings = new String[2];
        strings[0] = "test";
        return Response.ok(strings).build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().addAll(Arrays.asList(200));
        result.getEntityTypes().addAll(Arrays.asList("java.lang.String[]"));

        return Collections.singleton(result);
    }

}
