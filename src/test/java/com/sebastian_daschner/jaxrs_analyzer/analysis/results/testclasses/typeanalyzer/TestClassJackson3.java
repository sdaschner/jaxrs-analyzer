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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC,getterVisibility=Visibility.ANY)
public class TestClassJackson3 {

    @SuppressWarnings("unused")
    private String propOne;

    String propTwo;
    
    protected String propThree;
    
    public String propFour;

    @SuppressWarnings("unused")
    private int getInt() {
        return 0;
    }

    int getInt2() {
        return 0;
    }

    public static Set<TypeRepresentation> expectedTypeRepresentations() {
        final Map<String, TypeIdentifier> properties = new HashMap<>();
        properties.put("propThree", TypeUtils.STRING_IDENTIFIER);
        properties.put("propFour", TypeUtils.STRING_IDENTIFIER);
        properties.put("int", TypeUtils.INT_IDENTIFIER);
        properties.put("int2", TypeUtils.INT_IDENTIFIER);

        return Collections.singleton(TypeRepresentation.ofConcrete(expectedIdentifier(), properties));
    }

    public static TypeIdentifier expectedIdentifier() {
        return TypeIdentifier.ofType("Lcom/sebastian_daschner/jaxrs_analyzer/analysis/results/testclasses/typeanalyzer/TestClassJackson3;");
    }

}
