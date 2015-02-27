package com.sebastian_daschner.jaxrs_analyzer.analysis.project.classes.testclasses;

import com.sebastian_daschner.jaxrs_analyzer.builder.HttpResponseBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.builder.ClassResultBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.MethodResultBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("test")
public class TestClass1 {

    @GET
    public int method() {
        int status = 200;
        int anotherStatus = 100;
        status = anotherStatus = 300;
        return status;
    }

    public static ClassResult getResult() {
        final MethodResult methodResult = MethodResultBuilder.withResponses(HttpResponseBuilder.newBuilder().andEntityTypes("int").build())
                .andMethod(HttpMethod.GET).build();
        return ClassResultBuilder.withResourcePath("test").andMethods(methodResult).build();
    }

}
