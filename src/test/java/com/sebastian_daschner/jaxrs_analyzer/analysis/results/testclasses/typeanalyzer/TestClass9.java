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

import javax.json.Json;
import javax.json.JsonObject;
import java.time.LocalDate;
import java.time.ZonedDateTime;

public class TestClass9 {

    private boolean first;
    private String second;
    private ZonedDateTime time;
    private LocalDate date;

    public boolean isFirst() {
        return first;
    }

    public void setFirst(final boolean first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(final String second) {
        this.second = second;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(final ZonedDateTime time) {
        this.time = time;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    public static TypeRepresentation getResult() {
        final TypeRepresentation representation = new TypeRepresentation(TestClass9.class.getName());

        final JsonObject jsonObject = Json.createObjectBuilder().add("first", false).add("second", "string").add("time", "date")
                .add("date", "date").build();

        representation.getRepresentations().put("application/json", jsonObject);
        return representation;
    }

}
