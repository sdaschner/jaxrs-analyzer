package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

public class TestClass7 {

    private InnerTestClass6 innerTestClass;

    public Response method(final String id) {
        try {
            innerTestClass.method(id);
            return Response.noContent().build();
        } finally {
            Logger.getLogger("").info("deleted");
        }
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().add(204);

        return Collections.singleton(result);
    }

    private class InnerTestClass6 {

        private EntityManager entityManager;

        public void method(final String id) {
            final Object managedEntity = entityManager.find(Object.class, id);
            entityManager.remove(managedEntity);
            if ("".equals(id))
                throw new RuntimeException("");
        }

    }

}
