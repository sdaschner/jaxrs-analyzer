package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import java.util.Map;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;

public interface NormalizedTypeAnalyzer {

    Map<String, TypeIdentifier> analyzeClass(final String type, final Class<?> clazz);
    
}
