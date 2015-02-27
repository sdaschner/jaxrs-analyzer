package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.reduction;

import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection.ByteCodeCollector;
import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.TestClassUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

@RunWith(Parameterized.class)
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

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws NotFoundException, IOException, ReflectiveOperationException {
        Collection<Object[]> data = new LinkedList<>();
        final ByteCodeCollector byteCodeCollector = new ByteCodeCollector();

        final Set<Class<?>> testClasses = TestClassUtils.getClasses("com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.reduction.testclasses");

        testClassLabel:
        for (final Class<?> testClass : testClasses) {
            final Object[] testData = new Object[3];
            data.add(testData);

            testData[0] = testClass.getSimpleName();

            // load test class
            ClassPool pool = ClassPool.getDefault();
            final CtClass ctClass = pool.get(testClass.getCanonicalName());

            // "method"-method
            testData[1] = byteCodeCollector.buildInstructions(ctClass.getDeclaredMethod("method"));

            // take "instructions"-method with higher priority
            for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
                if ("instructions".equals(ctMethod.getName())) {
                    testData[2] = testClass.getDeclaredMethod("instructions").invoke(null);
                    continue testClassLabel;
                }
            }

            // "expected"#-methods
            testData[2] = new LinkedList<>();
            Stream.of(ctClass.getDeclaredMethods())
                    .filter(m -> m.getName().startsWith("expected")).sorted(Comparator.comparing(CtMethod::getName))
                    .map(byteCodeCollector::buildInstructions)
                    .forEach(((List<Instruction>) testData[2])::addAll);
        }

        return data;
    }

    @Test
    public void test() {
        final List<Instruction> actualVisitedInstructions = classUnderTest.reduceInstructions(allInstructions);

        Assert.assertEquals("failed for " + testClassName, visitedInstructions, actualVisitedInstructions);
    }

}
