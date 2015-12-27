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

import com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeIdentifierUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Type;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Types;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    public static Set<TypeRepresentation> expectedTypeRepresentations() {
        final Map<String, TypeIdentifier> properties = new HashMap<>();

        properties.put("first", TypeIdentifier.ofType(Types.PRIMITIVE_BOOLEAN));
        properties.put("second", TypeIdentifierUtils.STRING_IDENTIFIER);
        properties.put("time", TypeIdentifier.ofType(Types.DATE));
        properties.put("date", TypeIdentifier.ofType(Types.DATE));

        return Collections.singleton(TypeRepresentation.ofConcrete(expectedIdentifier(), properties));
    }

    public static TypeIdentifier expectedIdentifier() {
        return TypeIdentifier.ofType(new Type(TestClass9.class.getName()));
    }

}
