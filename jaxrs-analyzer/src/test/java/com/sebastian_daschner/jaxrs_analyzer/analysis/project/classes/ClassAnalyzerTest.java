package com.sebastian_daschner.jaxrs_analyzer.analysis.project.classes;


import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.TestClassUtils;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

@RunWith(Parameterized.class)
public class ClassAnalyzerTest {

    private final ClassAnalyzer classUnderTest;
    private final String testClassName;
    private final ClassResult expectedResult;
    private final CtClass ctClass;

    public ClassAnalyzerTest(final Class<?> clazz, final ClassResult expectedResult) throws NotFoundException {
        this.testClassName = clazz.getSimpleName();
        this.expectedResult = expectedResult;
        this.classUnderTest = new ClassAnalyzer();
        this.ctClass = ClassPool.getDefault().get(clazz.getCanonicalName());
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws NotFoundException, IOException, ReflectiveOperationException {
        Collection<Object[]> data = new LinkedList<>();

        final Set<Class<?>> testClasses = TestClassUtils.getClasses("com.sebastian_daschner.jaxrs_analyzer.analysis.project.classes.testclasses");

        for (final Class<?> testClass : testClasses) {
            if (!testClass.getSimpleName().startsWith("TestClass"))
                continue;

            final Object[] testData = new Object[2];

            testData[0] = testClass;

            // evaluate static "getResult"-method
            testData[1] = testClass.getDeclaredMethod("getResult").invoke(null);

            data.add(testData);
        }

        return data;
    }

    @Test
    public void test() {
        final ClassResult actualResult = classUnderTest.analyze(ctClass);

        Assert.assertEquals("failed for " + testClassName, expectedResult, actualResult);
    }

}
