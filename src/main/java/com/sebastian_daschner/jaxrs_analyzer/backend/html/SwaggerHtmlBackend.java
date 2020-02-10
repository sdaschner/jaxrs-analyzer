package com.sebastian_daschner.jaxrs_analyzer.backend.html;

import com.sebastian_daschner.jaxrs_analyzer.backend.swagger.SwaggerBackend;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.Project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class SwaggerHtmlBackend extends SwaggerBackend {

	private static final String NAME = "Html";

	private static final String HTML = "index.html";
	private static final String[] JS = {"swagger-ui-bundle.js", "swagger-ui-standalone-preset.js"};
	private static final String[] CSS = {"swagger-ui.css"};

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public byte[] render(Project project) {
		String html = getHtml()
				.replace("{{css}}", getAllCss())
				.replace("{{js}}", getAllJs())
				.replace("{{spec}}", getSwaggerJson(project));
		return html.getBytes(StandardCharsets.UTF_8);
	}

	private String getSwaggerJson(Project project) {
		StringWriter swaggerJsonWriter = new StringWriter();
		serialize(renderJson(project), swaggerJsonWriter, false);
		swaggerJsonWriter.flush();
		return swaggerJsonWriter.toString();
	}

	private String getHtml() {
		return getResourceAsString(HTML, null).toString();
	}

	private String getAllCss() {
		StringBuilder allCss = new StringBuilder();
		for (String j : CSS) {
			allCss.append("\n<style>\n");
			getResourceAsString(j, allCss);
			allCss.append("\n</style>\n");
		}
		return allCss.toString();
	}

	private String getAllJs() {
		StringBuilder allJs = new StringBuilder();
		for (String j : JS) {
			allJs.append("\n<script>\n");
			getResourceAsString(j, allJs);
			allJs.append("\n</script>\n");
		}
		return allJs.toString();
	}

	private static StringBuilder getResourceAsString(String name, StringBuilder appendTo) {
		appendTo = appendTo == null ? new StringBuilder() : appendTo;
		int startLen = appendTo.length();
		String fullName = "/html/" + name;
		try (InputStream resource = SwaggerHtmlBackend.class.getResourceAsStream(fullName)) {
			if (resource == null) {
				throw new RuntimeException("Failed to find resource " + fullName);
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
			String l;
			while ((l = reader.readLine()) != null) {
				appendTo.append(l).append("\n");
			}
			if (appendTo.length() - startLen == 0) {
				throw new RuntimeException("Failed to read resource " + fullName);
			}
			return appendTo;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
