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

import com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TestClass3 {

    private InnerClass first;

    private Type second;

    private AnotherInner third;

    public InnerClass getFirrst() {
        return first;
    }

    public Type getSeccond() {
        return second;
    }

    public static Set<TypeRepresentation> expectedTypeRepresentations() {
        final Map<String, TypeIdentifier> properties = new HashMap<>();

        final TypeIdentifier innerClassIdentifier = TypeIdentifier.ofType(InnerClass.class.getName());
        final TypeIdentifier typeIdentifier = TypeIdentifier.ofType(Type.class.getName());
        final TypeIdentifier anotherInnerIdentifier = TypeIdentifier.ofType(AnotherInner.class.getName());

        properties.put("first", innerClassIdentifier);
        properties.put("second", typeIdentifier);
        properties.put("third", anotherInnerIdentifier);

        final TypeRepresentation testClass3 = TypeRepresentation.ofConcrete(expectedIdentifier(), properties);
        final TypeRepresentation innerClass = TypeRepresentation.ofConcrete(innerClassIdentifier, Collections.singletonMap("name", TypeUtils.STRING_IDENTIFIER));
        final TypeRepresentation anotherInner = TypeRepresentation.ofConcrete(anotherInnerIdentifier);
        final TypeRepresentation type = TypeRepresentation.ofConcrete(typeIdentifier);

        return new HashSet<>(Arrays.asList(testClass3, innerClass, anotherInner, type));
    }

    public static TypeIdentifier expectedIdentifier() {
        return TypeIdentifier.ofType(TestClass3.class.getName());
    }

    public AnotherInner getThirrd() {
        return third;
    }

    private enum Type {
        ONE, TWO, THREE
    }

    private class InnerClass {
        private String name;

        public String getName() {
            return name;
        }
    }

    private class AnotherInner {
        private String notUsed;
    }
}
