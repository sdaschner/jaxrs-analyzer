package com.sebastian_daschner.jaxrs_analyzer.analysis.project.classes.testclasses;

import com.sebastian_daschner.jaxrs_analyzer.builder.ClassResultBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.HttpResponseBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.MethodResultBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("test")
@Produces(MediaType.APPLICATION_JSON)
public class TestClass3 {

    @GET
    public JsonObject method() {
        return Json.createObjectBuilder().add("key", "value").add("duke", 42).build();
    }

    public static ClassResult getResult() {
        final com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject jsonObject = new com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject();
        jsonObject.getStructure().put("key", new Element("java.lang.String", "value"));
        jsonObject.getStructure().put("duke", new Element("java.lang.Integer", 42));
        final MethodResult firstMethod = MethodResultBuilder.withResponses(HttpResponseBuilder.newBuilder().andEntityTypes("javax.json.JsonObject")
                .andInlineEntities(jsonObject).build())
                .andMethod(HttpMethod.GET).build();
        return ClassResultBuilder.withResourcePath("test").andResponseMediaTypes("application/json").andMethods(firstMethod).build();
    }

}
