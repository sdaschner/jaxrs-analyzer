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
import java.time.LocalDate;
import java.util.Date;

/**
 * @author Sebastian Daschner
 */
public class TestClass4 {

    private LocalDate first;

    private Date second;

    public LocalDate getFirst() {
        return first;
    }

    public Date getSecond() {
        return second;
    }

    public static TypeRepresentation getResult() {
        final TypeRepresentation representation = new TypeRepresentation(new Type(TestClass4.class.getName()));

        final JsonObject jsonObject = Json.createObjectBuilder().add("first", "date").add("second", "date").build();

        representation.getRepresentations().put("application/json", jsonObject);
        return representation;
    }

}
