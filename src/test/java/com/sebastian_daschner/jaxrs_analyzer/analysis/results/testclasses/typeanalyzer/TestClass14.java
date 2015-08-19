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

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Type;

import javax.json.Json;
import javax.json.JsonObject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class TestClass14 {

    private GenericFields<Long, String> longAndString;
    private GenericFields<String, Long> stringAndLong;

    public static TypeRepresentation getResult() {
        final TypeRepresentation representation = new TypeRepresentation(
                new Type(TestClass14.class.getName()));

        final JsonObject jsonLongAndString = Json.createObjectBuilder()
                .add("a", 0).add("b", "string")
                .add("listA", Json.createArrayBuilder().add(0)).build();
        final JsonObject jsonStringAndLong = Json.createObjectBuilder()
                .add("a", "string").add("b", 0)
                .add("listA", Json.createArrayBuilder().add("string")).build();

        final JsonObject jsonObject = Json.createObjectBuilder()
                .add("longAndString", jsonLongAndString)
                .add("stringAndLong", jsonStringAndLong).build();

        representation.getRepresentations().put("application/json", jsonObject);
        return representation;
    }

    public GenericFields<Long, String> getLongAndString() {
        return longAndString;
    }

    public GenericFields<String, Long> getStringAndLong() {
        return stringAndLong;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
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

}
