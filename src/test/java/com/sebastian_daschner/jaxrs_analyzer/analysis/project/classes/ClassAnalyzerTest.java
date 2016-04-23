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

package com.sebastian_daschner.jaxrs_analyzer.analysis.project.classes;


import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.BytecodeAnalyzer;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.JAXRSClassVisitor;
import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.TestClassUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ClassAnalyzerTest {

    private final String testClassSimpleName;
    private final String testClass;
    private final ClassResult expectedResult;

    public ClassAnalyzerTest(final String testClassSimpleName, final String testClass, final ClassResult expectedResult) throws NotFoundException {
        this.testClassSimpleName = testClassSimpleName;
        this.testClass = testClass;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() throws NotFoundException, IOException, ReflectiveOperationException {
        Collection<Object[]> data = new LinkedList<>();

        final Set<String> testClasses = TestClassUtils.getClasses("com/sebastian_daschner/jaxrs_analyzer/analysis/project/classes/testclasses");

        for (final String testClass : testClasses) {
            if (!testClass.contains("/TestClass"))
                continue;

            final Object[] testData = new Object[3];

            testData[0] = testClass.substring(testClass.lastIndexOf('/') + 1);
            testData[1] = testClass;

            // evaluate static "getResult"-method
            testData[2] = JavaUtils.loadClass(testClass).getDeclaredMethod("getResult").invoke(null);

            data.add(testData);
        }

        return data;
    }

    @Test
    public void test() throws IOException {
        final ClassReader classReader = new ClassReader(testClass);
        final ClassResult actualResult = new ClassResult();
        final ClassVisitor visitor = new JAXRSClassVisitor(actualResult);
        classReader.accept(visitor, ClassReader.EXPAND_FRAMES);
        new BytecodeAnalyzer().analyzeBytecode(actualResult);

        assertEquals(testClass, expectedResult, actualResult);
    }

}
