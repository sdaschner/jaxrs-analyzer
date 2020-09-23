package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode;

import com.sebastian_daschner.jaxrs_analyzer.analysis.JobRegistry;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.ContextClassReader;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.ProjectMethodClassVisitor;
import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;

import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sebastian_daschner.jaxrs_analyzer.analysis.utils.TestClassUtils.getClasses;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(Parameterized.class)
public class SubResourceLocatorMethodContentAnalyzerTest {

    private static JobRegistry originalJobRegistry;
    private final SubResourceLocatorMethodContentAnalyzer classUnderTest;
    private final String testClassSimpleName;
    private final String testClassName;
    private final Set<String> expectedClassNames;
    private final JobRegistry jobRegistry;
    private String signature;
    
    private static final VarHandle MODIFIERS;

    static {
        try {
            var lookup = MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup());
            MODIFIERS = lookup.findVarHandle(Field.class, "modifiers", int.class);
        } catch (IllegalAccessException | NoSuchFieldException ex) {
            throw new RuntimeException(ex);
        }
    }


    public SubResourceLocatorMethodContentAnalyzerTest(final String testClassSimpleName, final String testClassName, final String signature, final Set<String> expectedClassNames)
            throws ReflectiveOperationException {
        this.testClassSimpleName = testClassSimpleName;
        this.testClassName = testClassName;
        this.signature = signature;
        this.expectedClassNames = expectedClassNames;
        jobRegistry = mock(JobRegistry.class);
        this.classUnderTest = new SubResourceLocatorMethodContentAnalyzer();
        injectJobRegistry(jobRegistry);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() throws NotFoundException, IOException, ReflectiveOperationException {
        Collection<Object[]> data = new LinkedList<>();

        final Set<String> testClasses = getClasses("com/sebastian_daschner/jaxrs_analyzer/analysis/bytecode/subresource");

        for (final String testClass : testClasses) {
            final Object[] testData = new Object[4];

            final Class<?> loadedClass = JavaUtils.loadClassFromName(testClass);
            testData[0] = testClass.substring(testClass.lastIndexOf('/') + 1);
            testData[1] = testClass;
            testData[2] = Type.getMethodDescriptor(loadedClass.getDeclaredMethod("method"));
            testData[3] = loadedClass.getDeclaredMethod("getResult").invoke(null);

            data.add(testData);
        }

        return data;
    }

    @Test
    public void test() throws IOException {
        try {
            final ClassReader classReader = new ContextClassReader(testClassName);

            final MethodResult methodResult = new MethodResult();
            final ClassResult parentResource = new ClassResult();
            parentResource.setOriginalClass(testClassName);
            methodResult.setParentResource(parentResource);
            final ProjectMethodClassVisitor visitor = new ProjectMethodClassVisitor(methodResult, MethodIdentifier.of(testClassName, "method", signature, false));
            classReader.accept(visitor, ClassReader.EXPAND_FRAMES);

            classUnderTest.analyze(methodResult);
        } catch (Exception e) {
            System.err.println("failed for " + testClassName);
            throw e;
        }

        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(jobRegistry, atLeastOnce()).analyzeResourceClass(captor.capture(), any());

        assertEquals("failed for " + testClassName, expectedClassNames, captor.getAllValues().stream().collect(Collectors.toSet()));
        verify(jobRegistry, times(expectedClassNames.size())).analyzeResourceClass(any(), any());
    }

    @AfterClass
    public static void tearDown() throws NoSuchFieldException, IllegalAccessException {
        injectJobRegistry(originalJobRegistry);
    }

    private static void injectJobRegistry(final JobRegistry jobRegistry) throws NoSuchFieldException, IllegalAccessException {
        final Field field = JobRegistry.class.getDeclaredField("INSTANCE");
        field.setAccessible(true);
        originalJobRegistry = JobRegistry.getInstance();
        makeNonFinal(field);
        field.setAccessible(true);
        field.set(null, jobRegistry);
    }
    
    private static void makeNonFinal(Field field) {
        int mods = field.getModifiers();
        if (Modifier.isFinal(mods)) {
            MODIFIERS.set(field, mods & ~Modifier.FINAL);
        }
    }
}
