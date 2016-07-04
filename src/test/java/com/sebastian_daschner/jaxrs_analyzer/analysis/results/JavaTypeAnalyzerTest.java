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
import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class JavaTypeAnalyzerTest {

    private final JavaTypeAnalyzer classUnderTest;
    private final TypeIdentifier expectedIdentifier;
    private final Set<TypeRepresentation> expectedRepresentations;
    private final String testClassSimpleName;
    private final String testClassName;
    private final Map<TypeIdentifier, TypeRepresentation> actualTypeRepresentations;

    public JavaTypeAnalyzerTest(final String testClassSimpleName, final String testClassName, final TypeIdentifier expectedIdentifier,
                                final Set<TypeRepresentation> expectedRepresentations) throws NotFoundException {
        actualTypeRepresentations = new HashMap<>();
        this.testClassSimpleName = testClassSimpleName;
        this.testClassName = testClassName;
        this.expectedIdentifier = expectedIdentifier;
        this.expectedRepresentations = expectedRepresentations;
        this.classUnderTest = new JavaTypeAnalyzer(actualTypeRepresentations);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() throws NotFoundException, IOException, ReflectiveOperationException {
        Collection<Object[]> data = new LinkedList<>();

        final Set<String> testClasses = TestClassUtils.getClasses("com/sebastian_daschner/jaxrs_analyzer/analysis/results/testclasses/typeanalyzer");

        for (final String testClass : testClasses) {
            if (!testClass.contains("/TestClass"))
                continue;

            final Object[] testData = new Object[4];
            final Class<?> loadedClass = JavaUtils.loadClassFromName(testClass);
            testData[0] = testClass.substring(testClass.lastIndexOf('/') + 1);
            testData[1] = testClass;
            testData[2] = loadedClass.getDeclaredMethod("expectedIdentifier").invoke(null);
            testData[3] = loadedClass.getDeclaredMethod("expectedTypeRepresentations").invoke(null);

            data.add(testData);
        }

        return data;
    }

    @Test
    public void test() {
        final TypeIdentifier actualIdentifier;
        try {
            actualIdentifier = classUnderTest.analyze(JavaUtils.toType(testClassName));
        } catch (Exception e) {
            System.err.println("failed for " + testClassSimpleName);
            throw e;
        }

        assertEquals("failed for " + testClassSimpleName, expectedIdentifier, actualIdentifier);

        final Map<TypeIdentifier, TypeRepresentation> expectedTypeRepresentations = expectedRepresentations.stream()
                .collect(HashMap::new, (m, r) -> m.put(r.getIdentifier(), r), Map::putAll);
        assertEquals("failed for " + testClassSimpleName, expectedTypeRepresentations, actualTypeRepresentations);

        expectedRepresentations.stream().forEach(ex -> {
            final TypeRepresentation ac = actualTypeRepresentations.get(ex.getIdentifier());
            if (!TypeUtils.equals(ex, ac))
                fail("failed for " + testClassSimpleName + "\nNo type representation match \nexpected: " + ex + "\nactual:   " + ac);
        });
    }

}