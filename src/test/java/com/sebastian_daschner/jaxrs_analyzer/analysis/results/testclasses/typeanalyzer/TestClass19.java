package com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer;

import com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestClass19 extends SuperTestClass5 {

    private String foobar;

    public String getFoobar() {
        return foobar;
    }

    public void setFoobar(String foobar) {
        this.foobar = foobar;
    }

    public static Set<TypeRepresentation> expectedTypeRepresentations() {
        final Map<String, TypeIdentifier> properties = new HashMap<>();

        properties.put("foobar", TypeUtils.STRING_IDENTIFIER);
        properties.put("test", TypeUtils.STRING_IDENTIFIER);
        properties.put("hello", TypeUtils.STRING_IDENTIFIER);
        properties.put("overidden", TypeUtils.STRING_IDENTIFIER);

        return Collections.singleton(TypeRepresentation.ofConcrete(expectedIdentifier(), properties));
    }

    public static TypeIdentifier expectedIdentifier() {
        return TypeIdentifier.ofType("Lcom/sebastian_daschner/jaxrs_analyzer/analysis/results/testclasses/typeanalyzer/TestClass19;");
    }

}

class SuperTestClass5 extends SuperTestClass6 {

    private String test;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getOveridden() {
        return "Overidden return value";
    }

}

abstract class SuperTestClass6 {

    private String hello;

    public String getHello() {
        return hello;
    }

    public void setHello(String hello) {
        this.hello = hello;
    }

    public abstract Object getOveridden();

}