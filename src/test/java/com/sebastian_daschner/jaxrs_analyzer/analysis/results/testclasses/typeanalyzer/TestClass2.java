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

import javax.xml.bind.annotation.XmlTransient;
import java.util.Collections;
import java.util.Set;

public class TestClass2 {

    private String first;

    @XmlTransient // ignored due to XMLAccessorTypes#PUBLIC_MEMBER
    private String second;

    @XmlTransient
    public String third;

    @XmlTransient
    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    public static Set<TypeRepresentation> expectedTypeRepresentations() {
        return Collections.singleton(TypeRepresentation.ofConcrete(expectedIdentifier(), Collections.singletonMap("second", TypeUtils.STRING_IDENTIFIER)));
    }

    public static TypeIdentifier expectedIdentifier() {
        return TypeIdentifier.ofType(TestClass2.class.getName());
    }

}
