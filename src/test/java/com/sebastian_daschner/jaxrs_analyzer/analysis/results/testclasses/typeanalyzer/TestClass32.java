package com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import java.util.Collections;
import java.util.Set;

public class TestClass32 {

	private String name;
	private String notThisOne;

	public String getName() {
		return name;
	}

	public TestClass32 setName(String name) {
		this.name = name;
		return this;
	}

	public String getNotThisOne() {
		return notThisOne;
	}

	public TestClass32 setNotThisOne(String notThisOne) {
		this.notThisOne = notThisOne;
		return this;
	}

	public static Set<TypeRepresentation> expectedTypeRepresentations() {
		return Collections.singleton(TypeRepresentation.ofConcrete(expectedIdentifier()));
	}

	public static TypeIdentifier expectedIdentifier() {
		return TypeIdentifier.ofType(String.class);
	}
}
