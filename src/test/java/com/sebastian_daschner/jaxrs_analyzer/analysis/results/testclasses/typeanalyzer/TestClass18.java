package com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer;

import com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeIdentifierUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Type;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Types;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// TODO remove
@XmlAccessorType(XmlAccessType.FIELD)
public class TestClass18 extends SuperTestClass4 {

    private String foobar;
    private TestClass18 partner;

    public static Set<TypeRepresentation> expectedTypeRepresentations() {
        final Map<String, TypeIdentifier> properties = new HashMap<>();

        final TypeIdentifier identifier = expectedIdentifier();
        properties.put("foobar", TypeIdentifierUtils.STRING_IDENTIFIER);
        properties.put("test", TypeIdentifierUtils.STRING_IDENTIFIER);
        properties.put("partner", identifier);

        return Collections.singleton(TypeRepresentation.ofConcrete(identifier, properties));
    }

    public static TypeIdentifier expectedIdentifier() {
        return TypeIdentifier.ofType(new Type(TestClass18.class.getName()));
    }

}

@XmlAccessorType(XmlAccessType.FIELD)
class SuperTestClass4 {
    private String test;
}
