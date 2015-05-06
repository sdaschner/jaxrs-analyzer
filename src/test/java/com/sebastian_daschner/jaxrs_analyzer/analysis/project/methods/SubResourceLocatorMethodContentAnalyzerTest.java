package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods;

import com.sebastian_daschner.jaxrs_analyzer.analysis.project.classes.ClassAnalyzer;
import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.TestClassUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class SubResourceLocatorMethodContentAnalyzerTest {

    private final SubResourceLocatorMethodContentAnalyzer classUnderTest;
    private final String testClassName;
    private final String expectedClassName;
    private final CtMethod method;
    private final ClassAnalyzer classAnalyzer;

    public SubResourceLocatorMethodContentAnalyzerTest(final String testClassName, final CtMethod method, final String expectedClassName)
            throws ReflectiveOperationException {
        this.testClassName = testClassName;
        this.method = method;
        this.expectedClassName = expectedClassName;
        classAnalyzer = mock(ClassAnalyzer.class);
        this.classUnderTest = new SubResourceLocatorMethodContentAnalyzer();
        final Field field = SubResourceLocatorMethodContentAnalyzer.class.getDeclaredField("classAnalyzer");
        field.setAccessible(true);
        field.set(classUnderTest, classAnalyzer);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws NotFoundException, IOException, ReflectiveOperationException {
        Collection<Object[]> data = new LinkedList<>();

        final Set<Class<?>> testClasses = TestClassUtils.getClasses("com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods.testclasses.subresource");

        for (final Class<?> testClass : testClasses) {
            if (!testClass.getSimpleName().startsWith("TestClass"))
                continue;

            final Object[] testData = new Object[3];

            testData[0] = testClass.getSimpleName();

            // load test class
            ClassPool pool = ClassPool.getDefault();
            final CtClass ctClass = pool.get(testClass.getName());

            // "method"-method
            testData[1] = ctClass.getDeclaredMethod("method");

            // evaluate static "getResult"-method
            testData[2] = testClass.getDeclaredMethod("getResult").invoke(null);

            data.add(testData);
        }

        return data;
    }

    @Test
    public void test() {
        try {
            classUnderTest.analyze(method, new ClassResult());
        } catch (Exception e) {
            System.err.println("failed for " + testClassName);
            throw e;
        }

        final ArgumentCaptor<CtClass> captor = ArgumentCaptor.forClass(CtClass.class);
        verify(classAnalyzer).analyzeSubResource(captor.capture(), any());

        Assert.assertEquals("failed for " + testClassName, expectedClassName, captor.getValue().getName());
    }

}
