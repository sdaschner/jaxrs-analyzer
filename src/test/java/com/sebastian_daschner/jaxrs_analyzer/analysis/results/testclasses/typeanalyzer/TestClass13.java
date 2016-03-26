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
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import java.util.*;

public class TestClass13 {

    private GenericFields<Long, String> longAndString;
    private GenericFields<String, Long> stringAndLong;

    public GenericFields<Long, String> getLongAndString() {
        return longAndString;
    }

    public GenericFields<String, Long> getStringAndLong() {
        return stringAndLong;
    }

    private static class GenericFields<A, B> {

        private A a;

        private B b;

        private List<A> listA;

        public A getA() {
            return a;
        }

        public B getB() {
            return b;
        }

        public List<A> getListA() {
            return listA;
        }

    }

    public static Set<TypeRepresentation> expectedTypeRepresentations() {
        final Map<String, TypeIdentifier> properties = new HashMap<>();

        final TypeIdentifier longStringIdentifier = TypeIdentifier.ofType("com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer.TestClass13$GenericFields<java.lang.Long,java.lang.String>");
        final TypeIdentifier stringLongIdentifier = TypeIdentifier.ofType("com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer.TestClass13$GenericFields<java.lang.String,java.lang.Long>");
        final TypeIdentifier stringIdentifier = TypeUtils.STRING_IDENTIFIER;
        final TypeIdentifier longIdentifier = TypeIdentifier.ofType(Types.LONG);

        properties.put("longAndString", longStringIdentifier);
        properties.put("stringAndLong", stringLongIdentifier);

        final TypeRepresentation testClass13 = TypeRepresentation.ofConcrete(expectedIdentifier(), properties);

        final TypeIdentifier listStringIdentifier = TypeIdentifier.ofType("java.util.List<java.lang.String>");
        final TypeRepresentation listString = TypeRepresentation.ofCollection(listStringIdentifier, TypeRepresentation.ofConcrete(stringIdentifier));
        final TypeIdentifier listLongIdentifier = TypeIdentifier.ofType("java.util.List<java.lang.Long>");
        final TypeRepresentation listLong = TypeRepresentation.ofCollection(listLongIdentifier, TypeRepresentation.ofConcrete(longIdentifier));

        final Map<String, TypeIdentifier> longStringProperties = new HashMap<>();
        longStringProperties.put("a", longIdentifier);
        longStringProperties.put("b", stringIdentifier);
        longStringProperties.put("listA", listLongIdentifier);
        final TypeRepresentation longString = TypeRepresentation.ofConcrete(longStringIdentifier, longStringProperties);

        final Map<String, TypeIdentifier> stringLongProperties = new HashMap<>();
        stringLongProperties.put("a", stringIdentifier);
        stringLongProperties.put("b", longIdentifier);
        stringLongProperties.put("listA", listStringIdentifier);
        final TypeRepresentation stringLong = TypeRepresentation.ofConcrete(stringLongIdentifier, stringLongProperties);

        return new HashSet<>(Arrays.asList(testClass13, longString, stringLong, listLong, listString));
    }

    public static TypeIdentifier expectedIdentifier() {
        return TypeIdentifier.ofType(TestClass13.class.getName());
    }

}
