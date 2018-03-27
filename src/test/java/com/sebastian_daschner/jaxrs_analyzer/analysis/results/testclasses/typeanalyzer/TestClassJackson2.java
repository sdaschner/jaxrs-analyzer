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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

public class TestClassJackson2 {

    @JsonProperty("prop_one")
    private String propOne;

    private String propTwo;
    
    private String propThree;
    
    @JsonIgnore
    private String propFour;

    @JsonProperty("prop_five")
    public String propFive;

    public String getPropOne() {
        return propOne;
    }

    public void setPropOne(String propOne) {
        this.propOne = propOne;
    }

    @JsonProperty("prop_2G")
    public String getPropTwo() {
        return propTwo;
    }

    @JsonProperty("prop_2S")
    public void setPropTwo(String propTwo) {
        this.propTwo = propTwo;
    }

    public String getPropThree() {
        return propThree;
    }

    public void setPropThree(String propThree) {
        this.propThree = propThree;
    }

    public String getPropFour() {
        return propFour;
    }

    public void setPropFour(String propFour) {
        this.propFour = propFour;
    }



    public static Set<TypeRepresentation> expectedTypeRepresentations() {
        final Map<String, TypeIdentifier> properties = new HashMap<>();

        properties.put("prop_one", TypeUtils.STRING_IDENTIFIER);
        properties.put("prop_2G", TypeUtils.STRING_IDENTIFIER);
        properties.put("propThree", TypeUtils.STRING_IDENTIFIER);
        properties.put("prop_five", TypeUtils.STRING_IDENTIFIER);

        return Collections.singleton(TypeRepresentation.ofConcrete(expectedIdentifier(), properties));
    }

    public static TypeIdentifier expectedIdentifier() {
        return TypeIdentifier.ofType("Lcom/sebastian_daschner/jaxrs_analyzer/analysis/results/testclasses/typeanalyzer/TestClassJackson2;");
    }

}
