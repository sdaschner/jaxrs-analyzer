package com.sebastian_daschner.jaxrs_analyzer.analysis.project.classes.testclasses;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.builder.ClassResultBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.HttpResponseBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.MethodResultBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@Path("test")
public class TestClass11 extends ATestClass11 {

    @Override
    public Response getInfo(final String info) {
        return Response.ok(getContent(info)).build();
    }

    public static ClassResult getResult() {
        final MethodResult method = MethodResultBuilder.withResponses(HttpResponseBuilder.withStatues(200).andEntityTypes("Ljavax/ws/rs/core/GenericEntity;",
                "Ljavax/ws/rs/core/GenericEntity<Ljava/util/List<Ljava/lang/String;>;>;").build())
                .andPath("{info}").andMethod(HttpMethod.POST).andRequestBodyType(Types.STRING).build();
        return ClassResultBuilder.withResourcePath("test").andMethods(method).build();
    }

}

abstract class ATestClass11 {

    @POST
    @Path("{info}")
    public abstract Response getInfo(final String info);

    protected final GenericEntity<List<String>> getContent(final String content) {
        // TODO change to direct return, without variable declaration
        final GenericEntity<List<String>> entity = new GenericEntity<>(Collections.singletonList(content), List.class);
        return entity;
    }

}
