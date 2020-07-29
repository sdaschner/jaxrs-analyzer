package com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer;

import com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestClass31 {

	private String name;
	private String notThisOne;

	public String getName() {
		return name;
	}

	public TestClass31 setName(String name) {
		this.name = name;
		return this;
	}

	public String getNotThisOne() {
		return notThisOne;
	}

	public TestClass31 setNotThisOne(String notThisOne) {
		this.notThisOne = notThisOne;
		return this;
	}

	public static Set<TypeRepresentation> expectedTypeRepresentations() {
		final Map<String, TypeIdentifier> properties = new HashMap<>();

		properties.put("name", TypeUtils.STRING_IDENTIFIER);
		properties.put("anotherThing", TypeUtils.STRING_IDENTIFIER);

		return Collections.singleton(TypeRepresentation.ofConcrete(expectedIdentifier(), properties));
	}

	public static TypeIdentifier expectedIdentifier() {
		return TypeIdentifier.ofType("Lcom/sebastian_daschner/jaxrs_analyzer/analysis/results/testclasses/typeanalyzer/TestClass31;");
	}
}
