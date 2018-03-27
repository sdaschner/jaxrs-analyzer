package com.sebastian_daschner.jaxrs_analyzer.backend.swagger;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

final class TypeIdentifierTestSupport {

    private TypeIdentifierTestSupport() {
    }

    static void resetTypeIdentifierCounter() {
        try {
            Field dynamicCounterField = TypeIdentifier.class.getDeclaredField("dynamicCounter");
            dynamicCounterField.setAccessible(true);
            AtomicInteger dynamicCounter = (AtomicInteger) dynamicCounterField.get(null);
            dynamicCounter.set(0);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not reset the counter via reflection.", e);
        }
    }

}
