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

package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection;

import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.ProjectMethodClassVisitor;
import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.TestClassUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Stream;

import static com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier.of;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ByteCodeCollectorTest {

    private final String testClassSimpleName;
    private final String testClass;
    private final List<Instruction> expectedInstructions;

    public ByteCodeCollectorTest(final String testClassSimpleName, final String testClass, final List<Instruction> expectedInstructions) {
        this.testClassSimpleName = testClassSimpleName;
        this.testClass = testClass;
        this.expectedInstructions = expectedInstructions;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() throws NotFoundException, IOException, ReflectiveOperationException {
        Collection<Object[]> data = new LinkedList<>();

        final Set<String> testClasses = TestClassUtils.getClasses("com/sebastian_daschner/jaxrs_analyzer/analysis/bytecode/collection/testclasses");

        for (final String testClass : testClasses) {
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
        // round trip to find the correct test method
        final Method method = Stream.of(JavaUtils.loadClass(testClass).getDeclaredMethods()).filter(m -> m.getName().equals("method")).findAny().orElseThrow(NoSuchElementException::new);

        final ClassReader classReader = new ClassReader(testClass);
        final MethodResult methodResult = new MethodResult();
        final ProjectMethodClassVisitor visitor = new ProjectMethodClassVisitor(methodResult, of(testClass, "method", Type.getMethodDescriptor(method), false));
        classReader.accept(visitor, ClassReader.EXPAND_FRAMES);

        final List<Instruction> actualInstructions = methodResult.getInstructions();

        assertEquals(expectedInstructions, actualInstructions);
    }

}
