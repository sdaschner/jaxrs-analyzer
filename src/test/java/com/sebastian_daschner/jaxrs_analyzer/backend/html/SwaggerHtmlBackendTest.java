package com.sebastian_daschner.jaxrs_analyzer.backend.html;

import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;
import com.sebastian_daschner.jaxrs_analyzer.backend.swagger.SwaggerBackend;
import com.sebastian_daschner.jaxrs_analyzer.backend.swagger.SwaggerBackendTest;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Project;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Resources;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class SwaggerHtmlBackendTest {

    private final Backend cut;
	private final SwaggerBackend swaggerBackend;
    private final Resources resources;

    public SwaggerHtmlBackendTest(final Resources resources, String output, final Map<String, String> options) {
        cut = new SwaggerHtmlBackend();
        cut.configure(options);
        this.resources = resources;

	    swaggerBackend = new SwaggerBackend();
	    swaggerBackend.configure(options);
    }

    @Test
    public void test() throws IOException {
        final Project project = new Project("project name", "1.0", resources);
	    StringWriter stringWriter = new StringWriter();
	    cut.render(project, stringWriter);
	    final String actualOutput = stringWriter.toString();

	    stringWriter = new StringWriter();
        swaggerBackend.render(project, stringWriter);
        final String swaggerOutput = stringWriter.toString();
	    assertTrue(actualOutput.contains(swaggerOutput));
    }


    @Parameterized.Parameters
    public static Collection<Object[]> data() throws IOException {
        return SwaggerBackendTest.data();
    }

}