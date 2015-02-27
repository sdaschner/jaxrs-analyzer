package com.sebastian_daschner.jaxrs_analyzer.analysis.project.classes.testclasses;

import com.sebastian_daschner.jaxrs_analyzer.builder.HttpResponseBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.MethodResultBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.builder.ClassResultBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("test")
@Produces(MediaType.APPLICATION_JSON)
public class TestClass2 {

    @GET
    @Produces(MediaType.TEXT_HTML)
    public int method() {
        int status = 200;
        return status;
    }

    @POST
    public Response post(final String string) {
        return Response.accepted().build();
    }

    public static ClassResult getResult() {

        final MethodResult firstMethod = MethodResultBuilder.withResponses(HttpResponseBuilder.newBuilder().andEntityTypes("int").build())
                .andResponseMediaTypes("text/html").andMethod(HttpMethod.GET).build();
        final MethodResult secondMethod = MethodResultBuilder.withResponses(HttpResponseBuilder.withStatues(202).build())
                .andMethod(HttpMethod.POST).andRequestBodyType("java.lang.String").build();
        return ClassResultBuilder.withResourcePath("test").andResponseMediaTypes("application/json").andMethods(firstMethod, secondMethod).build();
    }

}
