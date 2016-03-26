package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods;

//@RunWith(Parameterized.class)
public class SubResourceLocatorMethodContentAnalyzerTest {

//    private final SubResourceLocatorMethodContentAnalyzer classUnderTest;
//    private final String testClassName;
//    private final Set<String> expectedClassNames;
//    private final CtMethod method;
//    private final ClassAnalyzer classAnalyzer;
//
//    public SubResourceLocatorMethodContentAnalyzerTest(final String testClassName, final CtMethod method, final Set<String> expectedClassNames)
//            throws ReflectiveOperationException {
//        this.testClassName = testClassName;
//        this.method = method;
//        this.expectedClassNames = expectedClassNames;
//        classAnalyzer = mock(ClassAnalyzer.class);
//        this.classUnderTest = new SubResourceLocatorMethodContentAnalyzer();
//        final Field field = SubResourceLocatorMethodContentAnalyzer.class.getDeclaredField("classAnalyzer");
//        field.setAccessible(true);
//        field.set(classUnderTest, classAnalyzer);
//    }
//
//    @Parameterized.Parameters
//    public static Collection<Object[]> data() throws NotFoundException, IOException, ReflectiveOperationException {
//        Collection<Object[]> data = new LinkedList<>();
//
//        final Set<Class<?>> testClasses = TestClassUtils.getClasses("com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.subresource");
//
//        for (final Class<?> testClass : testClasses) {
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
//        try {
//            classUnderTest.analyze(method, new ClassResult());
//        } catch (Exception e) {
//            System.err.println("failed for " + testClassName);
//            throw e;
//        }
//
//        final ArgumentCaptor<CtClass> captor = ArgumentCaptor.forClass(CtClass.class);
//        verify(classAnalyzer, atLeastOnce()).analyzeSubResource(captor.capture(), any());
//
//        Assert.assertEquals("failed for " + testClassName, expectedClassNames, captor.getAllValues().stream().map(CtClass::getName).collect(Collectors.toSet()));
//        verify(classAnalyzer, times(expectedClassNames.size())).analyzeSubResource(any(), any());
//    }

}
