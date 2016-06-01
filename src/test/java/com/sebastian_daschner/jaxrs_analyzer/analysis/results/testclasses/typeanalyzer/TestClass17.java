package com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer;

import com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import java.util.*;

public class TestClass17 extends SuperTestClass3 {

    private String world;
    private SuperTestClass3 partner;

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public SuperTestClass3 getPartner() {
        return partner;
    }

    public void setPartner(SuperTestClass3 partner) {
        this.partner = partner;
    }

    public static Set<TypeRepresentation> expectedTypeRepresentations() {
        final Map<String, TypeIdentifier> properties = new HashMap<>();

        final TypeIdentifier superTestClass3 = TypeIdentifier.ofType("Lcom/sebastian_daschner/jaxrs_analyzer/analysis/results/testclasses/typeanalyzer/SuperTestClass3;");
        final TypeIdentifier stringIdentifier = TypeUtils.STRING_IDENTIFIER;
        properties.put("hello", stringIdentifier);
        properties.put("world", stringIdentifier);
        properties.put("partner", superTestClass3);

        return new HashSet<>(Arrays.asList(TypeRepresentation.ofConcrete(expectedIdentifier(), properties),
                TypeRepresentation.ofConcrete(superTestClass3, Collections.singletonMap("hello", stringIdentifier))));
    }

    public static TypeIdentifier expectedIdentifier() {
        return TypeIdentifier.ofType("Lcom/sebastian_daschner/jaxrs_analyzer/analysis/results/testclasses/typeanalyzer/TestClass17;");
    }

}

class SuperTestClass3 {
    private String hello;

    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }

}
