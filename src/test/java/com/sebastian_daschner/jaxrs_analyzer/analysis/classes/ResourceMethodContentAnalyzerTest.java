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

package com.sebastian_daschner.jaxrs_analyzer.analysis.classes;


import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.BytecodeAnalyzer;
import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import static com.sebastian_daschner.jaxrs_analyzer.analysis.utils.TestClassUtils.getClasses;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ResourceMethodContentAnalyzerTest {

    private final String testClassSimpleName;
    private final String testClassName;
    private final Set<HttpResponse> expectedResult;

    @BeforeClass
    public static void setUpLogger() {
        LogProvider.injectDebugLogger(System.out::println);
    }

    public ResourceMethodContentAnalyzerTest(final String testClassSimpleName, final String testClassName, final Set<HttpResponse> expectedResult) {
        this.testClassSimpleName = testClassSimpleName;
        this.testClassName = testClassName;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() throws NotFoundException, IOException, ReflectiveOperationException {
        Collection<Object[]> data = new LinkedList<>();

        final Set<String> testClasses = getClasses("com/sebastian_daschner/jaxrs_analyzer/analysis/classes/testclasses/resource/response");
        testClasses.addAll(getClasses("com/sebastian_daschner/jaxrs_analyzer/analysis/classes/testclasses/resource/json"));
        testClasses.addAll(getClasses("com/sebastian_daschner/jaxrs_analyzer/analysis/classes/testclasses/resource/object"));

        for (final String testClass : testClasses) {
            if (!testClass.contains("/TestClass"))
                continue;

            final Object[] testData = new Object[3];

            testData[0] = testClass.substring(testClass.lastIndexOf('/') + 1);
            testData[1] = testClass;
            testData[2] = JavaUtils.loadClass(testClass).getDeclaredMethod("getResult").invoke(null);

            data.add(testData);
        }

        return data;
    }

    @Test
    public void test() throws IOException {
        try {
            final ClassReader classReader = new ClassReader(testClassName);
            final ClassResult classResult = new ClassResult();

            // only hook up to desired method
            final ClassVisitor visitor = new JAXRSClassVisitor(classResult) {
                @Override
                public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                    if ("method".equals(name))
                        return new JAXRSMethodVisitor(classResult, classResult.getOriginalClass(), desc, signature);
                    return null;
                }
            };

            classReader.accept(visitor, ClassReader.EXPAND_FRAMES);
            new BytecodeAnalyzer().analyzeBytecode(classResult);

            final Set<HttpResponse> actualResult = classResult.getMethods().iterator().next().getResponses();

            assertEquals(testClassName, expectedResult, actualResult);
        } catch (Exception e) {
            System.err.println("exception in " + testClassName);
            throw e;
        }
    }

}
