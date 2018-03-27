package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

public class JacksonAnalyzerFactory implements NormalizedTypeAnalyzerFactory {

    @Override
    public NormalizedTypeAnalyzer create(JavaTypeAnalyzer javaTypeAnalyzer) {
        return new JacksonAnalyzer(javaTypeAnalyzer);
    }

}
