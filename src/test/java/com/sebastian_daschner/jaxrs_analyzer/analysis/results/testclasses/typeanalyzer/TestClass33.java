package com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer;

import com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestClass33 {

	private TestClass32 swappedWithString;

	public TestClass32 getSwappedWithString() {
		return swappedWithString;
	}

	public TestClass33 setSwappedWithString(TestClass32 swappedWithString) {
		this.swappedWithString = swappedWithString;
		return this;
	}

	public static Set<TypeRepresentation> expectedTypeRepresentations() {
		final Map<String, TypeIdentifier> properties = new HashMap<>();
		properties.put("swappedWithString", TypeUtils.STRING_IDENTIFIER);
		return Collections.singleton(TypeRepresentation.ofConcrete(expectedIdentifier(), properties));
	}

	public static TypeIdentifier expectedIdentifier() {
		return TypeIdentifier.ofType(TestClass33.class);
	}
}
