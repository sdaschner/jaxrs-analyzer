package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.response;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

public class TestClass8 {

    private InnerTestClass6 innerTestClass;

    public Response method(final String id) {
        try {
            final int status = innerTestClass.method(id.length());
            return Response.status(status).build();
        } finally {
            Logger.getLogger("").info("deleted");
        }
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();

        result.getStatuses().add(404);

        return Collections.singleton(result);
    }

    private class InnerTestClass6 {

        private EntityManager entityManager;

        public int method(final int number) {
            synchronized (this) {
                System.out.println(3 * 2 / number);
                return 404;
            }
        }

    }

}
