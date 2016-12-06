package com.sebastian_daschner.jaxrs_analyzer;

import com.sebastian_daschner.jaxrs_analyzer.backend.Backend;
import com.sebastian_daschner.jaxrs_analyzer.backend.asciidoc.AsciiDocBackend;
import com.sebastian_daschner.jaxrs_analyzer.backend.plaintext.PlainTextBackend;
import com.sebastian_daschner.jaxrs_analyzer.backend.swagger.SwaggerBackend;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MainTest {

    @Test
    public void shouldAddBinaryAttributes() {
        final Map<String, String> conf = Main.addAttribute("att1 = val1");
        assertThat(conf.containsKey("att1"), is(true));
        assertThat(conf.get("att1"), is("val1"));
    }

    @Test
    public void shouldAddSingleAttributes() {
        final Map<String, String> conf = Main.addAttribute("att1");
        assertThat(conf.containsKey("att1"), is(true));
        assertThat(conf.get("att1"), is(""));
    }

    @Test
    public void shouldAddEmptyAttributeValues() {
        final Map<String, String> conf = Main.addAttribute("att1=");
        assertThat(conf.containsKey("att1"), is(true));
        assertThat(conf.get("att1"), is(""));
    }

    @Test
    public void shouldLoadSwaggerFromJavaService() {
        final Backend backend = JAXRSAnalyzer.constructBackend("swagger");
        assertThat(backend, is(instanceOf(SwaggerBackend.class)));
    }

    @Test
    public void shouldLoadPlainTextFromJavaService() {
        final Backend backend = JAXRSAnalyzer.constructBackend("plaintext");
        assertThat(backend, is(instanceOf(PlainTextBackend.class)));
    }

    @Test
    public void shouldLoadAsciiDocFromJavaService() {
        final Backend backend = JAXRSAnalyzer.constructBackend("asciidoc");
        assertThat(backend, is(instanceOf(AsciiDocBackend.class)));
    }

}
