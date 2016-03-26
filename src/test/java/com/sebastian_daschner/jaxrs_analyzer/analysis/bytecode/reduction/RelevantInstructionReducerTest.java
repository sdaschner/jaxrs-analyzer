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

package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.reduction;

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

//@RunWith(Parameterized.class)
public class RelevantInstructionReducerTest {

    private final RelevantInstructionReducer classUnderTest;
    private final String testClassName;
    private final List<Instruction> visitedInstructions;
    private final List<Instruction> allInstructions;

    public RelevantInstructionReducerTest(final String testClassName, final List<Instruction> allInstructions, final List<Instruction> visitedInstructions) {
        this.testClassName = testClassName;
        this.allInstructions = allInstructions;
        this.visitedInstructions = visitedInstructions;
        this.classUnderTest = new RelevantInstructionReducer();
    }

//    @Parameterized.Parameters
//    public static Collection<Object[]> data() throws NotFoundException, IOException, ReflectiveOperationException {
//        Collection<Object[]> data = new LinkedList<>();
//        final ByteCodeCollector byteCodeCollector = new ByteCodeCollector();
//
//        final Set<Class<?>> testClasses = TestClassUtils.getClasses("com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.reduction.testclasses");
//
//        testClassLabel:
//        for (final Class<?> testClass : testClasses) {
//            final Object[] testData = new Object[3];
//            data.add(testData);
//
//            testData[0] = testClass.getSimpleName();
//
//            // load test class
//            ClassPool pool = ClassPool.getDefault();
//            final CtClass ctClass = pool.get(testClass.getCanonicalName());
//
//            // "method"-method
//            testData[1] = byteCodeCollector.buildInstructions(ctClass.getDeclaredMethod("method"));
//
//            // take "instructions"-method with higher priority
//            for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
//                if ("instructions".equals(ctMethod.getName())) {
//                    testData[2] = testClass.getDeclaredMethod("instructions").invoke(null);
//                    continue testClassLabel;
//                }
//            }
//
//            // "expected"#-methods
//            testData[2] = new LinkedList<>();
//            Stream.of(ctClass.getDeclaredMethods())
//                    .filter(m -> m.getName().startsWith("expected")).sorted(Comparator.comparing(CtMethod::getName))
//                    .map(byteCodeCollector::buildInstructions)
//                    .forEach(((List<Instruction>) testData[2])::addAll);
//        }
//
//        return data;
//    }
//
//    @Test
//    public void test() {
//        final List<Instruction> actualVisitedInstructions = classUnderTest.reduceInstructions(allInstructions);
//
//        Assert.assertEquals("failed for " + testClassName, visitedInstructions, actualVisitedInstructions);
//    }

}
