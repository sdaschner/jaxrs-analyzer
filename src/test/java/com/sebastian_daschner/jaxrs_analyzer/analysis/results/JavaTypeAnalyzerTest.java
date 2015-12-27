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

package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.TestClassUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Type;
import javassist.NotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.*;

@RunWith(Parameterized.class)
public class JavaTypeAnalyzerTest {

    private final JavaTypeAnalyzer classUnderTest;
    private final TypeIdentifier expectedIdentifier;
    private final Set<TypeRepresentation> expectedRepresentations;
    private final Class<?> clazz;
    private final Map<TypeIdentifier, TypeRepresentation> actualTypeRepresentations;

    public JavaTypeAnalyzerTest(final Class<?> clazz, final TypeIdentifier expectedIdentifier, final Set<TypeRepresentation> expectedRepresentations) throws NotFoundException {
        actualTypeRepresentations = new HashMap<>();
        this.expectedIdentifier = expectedIdentifier;
        this.expectedRepresentations = expectedRepresentations;
        this.classUnderTest = new JavaTypeAnalyzer(actualTypeRepresentations);
        this.clazz = clazz;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws NotFoundException, IOException, ReflectiveOperationException {
        Collection<Object[]> data = new LinkedList<>();

        final Set<Class<?>> testClasses = TestClassUtils.getClasses("com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer");

        for (final Class<?> testClass : testClasses) {
            if (!testClass.getSimpleName().startsWith("TestClass"))
                continue;

            final Object[] testData = new Object[3];

            testData[0] = testClass;
            testData[1] = testClass.getDeclaredMethod("expectedIdentifier").invoke(null);
            testData[2] = testClass.getDeclaredMethod("expectedTypeRepresentations").invoke(null);

            data.add(testData);
        }

        return data;
    }

    @Test
    public void test() {
        final TypeIdentifier actualIdentifier;
        try {
            actualIdentifier = classUnderTest.analyze(new Type(clazz.getName()));
        } catch (Exception e) {
            System.err.println("failed for " + clazz.getSimpleName());
            throw e;
        }

        Assert.assertEquals("failed for " + clazz.getSimpleName(), expectedIdentifier, actualIdentifier);
        final Map<TypeIdentifier, TypeRepresentation> expectedTypeRepresentations = expectedRepresentations.stream()
                .collect(HashMap::new, (m, r) -> m.put(r.getIdentifier(), r), Map::putAll);
        Assert.assertEquals("failed for " + clazz.getSimpleName(), expectedTypeRepresentations, actualTypeRepresentations);
    }

}