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


//@RunWith(Parameterized.class)
public class ClassAnalyzerTest {

//    private final ClassAnalyzer classUnderTest;
//    private final String testClassName;
//    private final ClassResult expectedResult;
//    private final CtClass ctClass;
//
//    public ClassAnalyzerTest(final Class<?> clazz, final ClassResult expectedResult) throws NotFoundException {
//        this.testClassName = clazz.getSimpleName();
//        this.expectedResult = expectedResult;
//        this.classUnderTest = new ClassAnalyzer();
//        this.ctClass = ClassPool.getDefault().get(clazz.getCanonicalName());
//    }
//
//    @Parameterized.Parameters
//    public static Collection<Object[]> data() throws NotFoundException, IOException, ReflectiveOperationException {
//        Collection<Object[]> data = new LinkedList<>();
//
//        final Set<Class<?>> testClasses = TestClassUtils.getClasses("com.sebastian_daschner.jaxrs_analyzer.analysis.project.classes.testclasses");
//
//        for (final Class<?> testClass : testClasses) {
//            if (!testClass.getSimpleName().startsWith("TestClass"))
//                continue;
//
//            final Object[] testData = new Object[2];
//
//            testData[0] = testClass;
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
//        final ClassResult actualResult = classUnderTest.analyze(ctClass);
//
//        Assert.assertEquals("failed for " + testClassName, expectedResult, actualResult);
//    }

}
