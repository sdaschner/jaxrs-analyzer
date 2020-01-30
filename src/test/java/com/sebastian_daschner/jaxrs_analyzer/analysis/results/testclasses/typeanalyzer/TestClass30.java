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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Test @JsonIgnore with several methods & different cases
 */
public class TestClass30 {

    private static String PRIVATE_FIELD;
    public static String PUBLIC_FIELD;
    private String privateField;
    protected String protectedField;

    public String publicField;

    @JsonIgnore
    private boolean testName;
    private boolean testname;

    public boolean isTestName() {
        return testName;
    }

    public boolean isTestname() {
        return testname;
    }

    public int getInt() {
        return 0;
    }

    public static String getStaticString() {
        return null;
    }

    public String string() {
        return null;
    }

    public static Set<TypeRepresentation> expectedTypeRepresentations() {
        final Map<String, TypeIdentifier> properties = new HashMap<>();

        properties.put("publicField", TypeUtils.STRING_IDENTIFIER);
        properties.put("int", TypeIdentifier.ofType(Types.PRIMITIVE_INT));
        properties.put("testname", TypeIdentifier.ofType(Types.PRIMITIVE_BOOLEAN));

        return Collections.singleton(TypeRepresentation.ofConcreteBuilder().identifier(expectedIdentifier()).properties(properties).build());
    }

    public static TypeIdentifier expectedIdentifier() {
        return TypeIdentifier.ofType("Lcom/sebastian_daschner/jaxrs_analyzer/analysis/results/testclasses/typeanalyzer/TestClass30;");
    }

}
