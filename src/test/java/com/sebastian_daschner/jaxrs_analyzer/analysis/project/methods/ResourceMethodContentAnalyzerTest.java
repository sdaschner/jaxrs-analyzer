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

package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods;


//@RunWith(Parameterized.class)
public class ResourceMethodContentAnalyzerTest {

//    private final ResourceMethodContentAnalyzer classUnderTest;
//    private final String testClassName;
//    private final Set<HttpResponse> expectedResult;
//    private final CtMethod method;
//
//    public ResourceMethodContentAnalyzerTest(final String testClassName, final CtMethod method, final Set<HttpResponse> expectedResult) {
//        this.testClassName = testClassName;
//        this.expectedResult = expectedResult;
//        this.classUnderTest = new ResourceMethodContentAnalyzer();
//        this.method = method;
//    }
//
//    @Parameterized.Parameters
//    public static Collection<Object[]> data() throws NotFoundException, IOException, ReflectiveOperationException {
//        Collection<Object[]> data = new LinkedList<>();
//
//        final Set<Class<?>> testClasses = TestClassUtils.getClasses("com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.resource.response");
//        testClasses.addAll(TestClassUtils.getClasses("com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.resource.json"));
//        testClasses.addAll(TestClassUtils.getClasses("com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.resource.object"));
//
//        for (final Class<?> testClass : testClasses) {
//            if (!testClass.getSimpleName().startsWith("TestClass"))
//                continue;
//
//            final Object[] testData = new Object[3];
//
//            testData[0] = testClass.getSimpleName();
//
//            // load test class
//            ClassPool pool = ClassPool.getDefault();
//            final CtClass ctClass = pool.get(testClass.getName());
//
//            // "method"-method
//            testData[1] = ctClass.getDeclaredMethod("method");
//
//            // evaluate static "getResult"-method
//            testData[2] = testClass.getDeclaredMethod("getResult").invoke(null);
//
//            data.add(testData);
//        }
//
//        return data;
//    }
//
//    @Test
//    public void test() {
//        final MethodResult result = new MethodResult();
//        try {
//            classUnderTest.analyze(method, result);
//        } catch (Throwable e) {
//            System.err.println("exception in " + testClassName);
//            throw e;
//        }
//        final Set<HttpResponse> actualResult = result.getResponses();
//
//        Assert.assertEquals("failed for " + testClassName, expectedResult, actualResult);
//    }

}
