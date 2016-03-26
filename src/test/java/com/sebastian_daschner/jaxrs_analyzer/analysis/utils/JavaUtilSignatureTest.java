package com.sebastian_daschner.jaxrs_analyzer.analysis.utils;

import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class JavaUtilSignatureTest {

    private final String signature;
    private final List<String> parameterTypes;

    public JavaUtilSignatureTest(final String signature, final List<String> parameterTypes) {
        this.signature = signature;
        this.parameterTypes = parameterTypes;
    }

    @Test
    public void testGetParameters() {
        try {
            assertThat(JavaUtils.getParameters(signature), is(parameterTypes));
        } catch (Exception e) {
            System.err.println("Failed for " + signature);
            throw e;
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        final Collection<Object[]> data = new LinkedList<>();

        data.add(testData("(Z)V", singletonList("Z")));
        data.add(testData("([Z)V", singletonList("[Z")));
        data.add(testData("(Ljava/lang/String;)V", singletonList("Ljava/lang/String;")));
        data.add(testData("(Ljava/lang/String;Ljava/util/List;)V", asList("Ljava/lang/String;", "Ljava/util/List;")));
        data.add(testData("(Ljava/util/List<Ljava/lang/String;>;)V", singletonList("Ljava/util/List<Ljava/lang/String;>;")));
        data.add(testData("([Ljava/util/List<Ljava/lang/String;>;)V", singletonList("[Ljava/util/List<Ljava/lang/String;>;")));
        data.add(testData("(Ljava/util/Map<Ljava/lang/String;Ljava/util/List<Ljava/lang/String;>;>;)Ljavax/ws/rs/core/Response;",
                singletonList("Ljava/util/Map<Ljava/lang/String;Ljava/util/List<Ljava/lang/String;>;>;")));
        data.add(testData("([Ljava/util/Map<Ljava/lang/String;Ljava/util/List<Ljava/lang/String;>;>;)Ljavax/ws/rs/core/Response;",
                singletonList("[Ljava/util/Map<Ljava/lang/String;Ljava/util/List<Ljava/lang/String;>;>;")));

        return data;
    }

    private static Object[] testData(String signature, List<String> types) {
        final Object[] testData = new Object[2];
        testData[0] = signature;
        testData[1] = types;
        return testData;
    }

}
