package com.sebastian_daschner.jaxrs_analyzer.analysis.javadoc;

import com.sebastian_daschner.jaxrs_analyzer.utils.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class ResponseCommentExtractorTest {

    @Parameterized.Parameter
    public String commentText;

    @Parameterized.Parameter(1)
    public Pair<Integer, String> expected;

    @Test
    public void testExtract() {
        assertThat(ResponseCommentExtractor.extract(commentText), is(expected));
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return asList(
                data("300 hello world", 300, "hello world"),
                data("200 hello world", 200, "hello world"),
                data("200  hello world ", 200, "hello world"),
                data(" 200  hello world ", 200, "hello world"),
                data("\t200\thello\tworld ", 200, "hello\tworld"),
                nullData("hello world")
        );
    }

    private static Object[] data(String commentText, int status, String comment) {
        return new Object[]{commentText, Pair.of(status, comment)};
    }

    private static Object[] nullData(String commentText) {
        return new Object[]{commentText, null};
    }

}