package com.sebastian_daschner.jaxrs_analyzer.analysis.project.classes.testclasses;

import com.sebastian_daschner.jaxrs_analyzer.builder.HttpResponseBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.builder.ClassResultBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.MethodResultBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("test")
public class TestClass4 {

    @GET
    @Path("{info}")
    public Response getInfo(final String info) {
        return Response.ok().header("X-Info", info + " is complex").build();
    }

    public static ClassResult getResult() {
        final MethodResult method = MethodResultBuilder.withResponses(HttpResponseBuilder.withStatues(200).andHeaders("X-Info").build()).andPath("{info}")
                .andMethod(HttpMethod.GET).andRequestBodyType("java.lang.String").build();
        return ClassResultBuilder.withResourcePath("test").andMethods(method).build();
    }

}
