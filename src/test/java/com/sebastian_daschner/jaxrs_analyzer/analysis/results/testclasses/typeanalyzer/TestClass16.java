package com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer;

import com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.*;

public class TestClass16 extends SuperTestClass2 {

    private String world;
    private SuperTestClass2 partner;

    public static Set<TypeRepresentation> expectedTypeRepresentations() {
        final Map<String, TypeIdentifier> properties = new HashMap<>();

        final TypeIdentifier superTestClass2 = TypeIdentifier.ofType("Lcom/sebastian_daschner/jaxrs_analyzer/analysis/results/testclasses/typeanalyzer/SuperTestClass2;");
        final TypeIdentifier stringIdentifier = TypeUtils.STRING_IDENTIFIER;
        properties.put("hello", stringIdentifier);
        properties.put("world", stringIdentifier);
        properties.put("partner", superTestClass2);

        return new HashSet<>(Arrays.asList(TypeRepresentation.ofConcrete(expectedIdentifier(), properties),
                TypeRepresentation.ofConcrete(superTestClass2, Collections.singletonMap("hello", stringIdentifier))));
    }

    public static TypeIdentifier expectedIdentifier() {
        return TypeIdentifier.ofType("Lcom/sebastian_daschner/jaxrs_analyzer/analysis/results/testclasses/typeanalyzer/TestClass16;");
    }

}

@XmlAccessorType(XmlAccessType.FIELD)
class SuperTestClass2 {
    private String hello;
}
