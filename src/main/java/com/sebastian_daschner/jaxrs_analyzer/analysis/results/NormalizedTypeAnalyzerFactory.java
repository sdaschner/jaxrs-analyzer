package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

public interface NormalizedTypeAnalyzerFactory {

    NormalizedTypeAnalyzer create(JavaTypeAnalyzer javaTypeAnalyzer);
    
}
