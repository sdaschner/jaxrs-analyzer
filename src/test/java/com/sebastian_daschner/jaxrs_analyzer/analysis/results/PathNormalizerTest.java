package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.builder.ClassResultBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.HttpResponseBuilder;
import com.sebastian_daschner.jaxrs_analyzer.builder.MethodResultBuilder;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.HttpMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class PathNormalizerTest {

    @Parameterized.Parameter
    public String path;
    @Parameterized.Parameter(1)
    public String methodPath;
    @Parameterized.Parameter(2)
    public String nestedMethodPath;
    @Parameterized.Parameter(3)
    public String expectedPath;

    @Test
    public void test() {
        final ClassResult classResults = buildClassStructure(path, methodPath, nestedMethodPath);
        final MethodResult methodResult = findDeepestMethodResult(classResults);

        assertThat(PathNormalizer.getPath(methodResult), is(expectedPath));
    }

    @Parameterized.Parameters(name = "{0}, {1}, {2} -> {3}")
    public static Collection<Object[]> testData() {
        return asList(
                new Object[]{"/test", "world", "path", "test/world/path"},
                new Object[]{"/", "/world", "path", "world/path"},
                new Object[]{"//hello", "world", "path", "hello/world/path"},
                new Object[]{"test", "hello", "{world}", "test/hello/{world}"},
                new Object[]{"test", "hello", "{world:[foobar]+}", "test/hello/{world}"},
                new Object[]{"test", "hello", "{world:[a-z]+}", "test/hello/{world}"},
                new Object[]{"test", "hello", "hello/world/{world:[a-z]+}-{id}", "test/hello/hello/world/{world}-{id}"},
                new Object[]{"//test", "hello", "/hello/world/{world:[a-z]+}-{id}", "test/hello/hello/world/{world}-{id}"},
                new Object[]{"test", "hello", "hello/{world:[^\\{\\}a-z]+}-{id}", "test/hello/hello/{world}-{id}"},
                new Object[]{"test", "hello", "{world:.*}", "test/hello/{world}"},
                new Object[]{"test", "hello", "{world:.*}-{id}//", "test/hello/{world}-{id}"},
                new Object[]{"test", "hello", "///{world:.*}-{id}//", "test/hello/{world}-{id}"},
                new Object[]{"test", "hello", "///{world:.*}/-{id}//", "test/hello/{world}/-{id}"},
                new Object[]{"test", "hello", "{world:.*}-{id}/a//", "test/hello/{world}-{id}/a"}
        );
    }

    private static ClassResult buildClassStructure(final String path, final String methodPath, final String nestedMethodPath) {
        final MethodResult nestedMethod = MethodResultBuilder.withResponses(HttpResponseBuilder.withStatues(204).build()).andPath(nestedMethodPath).andMethod(HttpMethod.POST).build();
        final ClassResult nestedClassResult = ClassResultBuilder.withResourcePath("ignored").andMethods(nestedMethod).build();
        final MethodResult method = MethodResultBuilder.newBuilder().andPath(methodPath).build();
        method.setSubResource(nestedClassResult);
        return ClassResultBuilder.withResourcePath(path).andMethods(method).build();
    }

    private static MethodResult findDeepestMethodResult(final ClassResult classResults) {
        ClassResult current = classResults;
        MethodResult result = null;
        while (current != null && !current.getMethods().isEmpty()) {
            result = current.getMethods().iterator().next();
            current = result.getSubResource();
        }

        return result;
    }

}