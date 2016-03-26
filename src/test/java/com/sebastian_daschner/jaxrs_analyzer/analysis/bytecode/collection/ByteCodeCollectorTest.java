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

//@RunWith(Parameterized.class)
public class ByteCodeCollectorTest {

//    private ByteCodeCollector classUnderTest;
//    private final String testClassName;
//    private final List<Instruction> expectedInstructions;
//    private final CtBehavior method;
//
//    public ByteCodeCollectorTest(final CtBehavior testMethod, final List<Instruction> expectedInstructions) {
//        this.expectedInstructions = expectedInstructions;
//        this.testClassName = testMethod.getDeclaringClass().getSimpleName();
//        this.classUnderTest = new ByteCodeCollector();
//        method = testMethod;
//    }
//
//    @Parameterized.Parameters
//    public static Collection<Object[]> data() throws NotFoundException, IOException, ReflectiveOperationException {
//        Collection<Object[]> data = new LinkedList<>();
//
//        final Set<Class<?>> testClasses = TestClassUtils.getClasses("com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection.testclasses");
//
//        for (final Class<?> testClass : testClasses) {
//            final Object[] testData = new Object[2];
//
//            // load test class
//            ClassPool pool = ClassPool.getDefault();
//            final CtClass ctClass = pool.get(testClass.getCanonicalName());
//
//            // "method"-method
//            testData[0] = ctClass.getDeclaredMethod("method");
//
//            // evaluate static "getResult"-method
//            testData[1] = testClass.getDeclaredMethod("getResult").invoke(null);
//
//            data.add(testData);
//        }
//
//        return data;
//    }
//
//    @Test
//    public void test() {
//        final List<Instruction> actualInstructions = classUnderTest.buildInstructions(method);
//
//        Assert.assertEquals("failed for " + testClassName, expectedInstructions, actualInstructions);
//    }

}
