package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.TestClassUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
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
public class TypeAnalyzerTest {

    private final TypeAnalyzer classUnderTest;
    private final TypeRepresentation expectedResult;
    private final Class<?> clazz;

    public TypeAnalyzerTest(final Class<?> clazz, final TypeRepresentation expectedResult) throws NotFoundException {
        this.expectedResult = expectedResult;
        this.classUnderTest = new TypeAnalyzer();
        this.clazz = clazz;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws NotFoundException, IOException, ReflectiveOperationException {
        Collection<Object[]> data = new LinkedList<>();

        final Set<Class<?>> testClasses = TestClassUtils.getClasses("com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer");

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
        final TypeRepresentation actualResult;
        try {
            actualResult = classUnderTest.analyze(clazz.getName());
        } catch (Exception e) {
            System.err.println("failed for " + clazz.getSimpleName());
            throw e;
        }

        Assert.assertEquals("failed for " + clazz.getSimpleName(), expectedResult, actualResult);
    }

}