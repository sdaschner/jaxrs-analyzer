package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.resource.object;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestClass11 {

    private StateMachineStore sms;

    public Response method(@PathParam("stateMachineId") String stateMachineId, @Context UriInfo info, JsonObject attachment) {
        boolean successfullyAttached = attach(stateMachineId, attachment);
        if (!successfullyAttached) {
            return Response.status(Response.Status.BAD_REQUEST).
                    header("info", "State machine with " + stateMachineId + " not found").
                    build();
        }
        URI uri = info.getAbsolutePathBuilder().build();
        return Response.created(uri).build();
    }

    public boolean attach(String stateMachineId, JsonObject attachment) {
        StateMachine machine = sms.findStateMachine(stateMachineId);
        if (machine == null) {
            return false;
        }
        try {
            byte[] serialized = serialize(attachment);
            machine.setAttachment(serialized);
        } catch (IOException ex) {
            throw new IllegalStateException("Cannot serialize attachment", ex);
        }
        return true;
    }

    byte[] serialize(JsonObject object) throws IOException {
        try (ByteArrayOutputStream oos = new ByteArrayOutputStream(); JsonWriter writer = Json.createWriter(oos)) {
            writer.writeObject(object);
            writer.close();
            oos.flush();
            return oos.toByteArray();
        }
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse firstResponse = new HttpResponse();
        firstResponse.getStatuses().add(400);
        firstResponse.getHeaders().add("info");

        final HttpResponse secondResponse = new HttpResponse();
        secondResponse.getStatuses().add(201);
        secondResponse.getHeaders().add("Location");

        return new HashSet<>(Arrays.asList(firstResponse, secondResponse));
    }

    private class StateMachine {
        public void setAttachment(byte[] serialized) {

        }
    }

    private interface StateMachineStore {
        StateMachine findStateMachine(final String stateMachineId);
    }
}
