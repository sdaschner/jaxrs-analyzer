/*
 * Copyright (C) 2015 Sebastian Daschner, sebastian-daschner.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class TestClass12 {

    private int first;
    private InnerTestClass child;

    @XmlAccessorType(XmlAccessType.FIELD)
    private class InnerTestClass {
        private int second;
        private TestClass12 child;
    }

    public static Set<TypeRepresentation> expectedTypeRepresentations() {
        final Map<String, TypeIdentifier> properties = new HashMap<>();

        final TypeIdentifier innerTestIdentifier = TypeIdentifier.ofType("Lcom/sebastian_daschner/jaxrs_analyzer/analysis/results/testclasses/typeanalyzer/TestClass12$InnerTestClass;");
        properties.put("first", TypeIdentifier.ofType(Types.PRIMITIVE_INT));
        properties.put("child", innerTestIdentifier);

        final TypeIdentifier testClass12Identifier = expectedIdentifier();
        final TypeRepresentation testClass12 = TypeRepresentation.ofConcreteBuilder().identifier(testClass12Identifier).properties(properties).build();

        final Map<String, TypeIdentifier> innerProperties = new HashMap<>();
        innerProperties.put("second", TypeIdentifier.ofType(Types.PRIMITIVE_INT));
        innerProperties.put("child", testClass12Identifier);

        final TypeRepresentation innerTestClass = TypeRepresentation.ofConcreteBuilder().identifier(innerTestIdentifier).properties(innerProperties).build();

        return new HashSet<>(Arrays.asList(testClass12, innerTestClass));
    }

    public static TypeIdentifier expectedIdentifier() {
        return TypeIdentifier.ofType("Lcom/sebastian_daschner/jaxrs_analyzer/analysis/results/testclasses/typeanalyzer/TestClass12;");
    }

}
