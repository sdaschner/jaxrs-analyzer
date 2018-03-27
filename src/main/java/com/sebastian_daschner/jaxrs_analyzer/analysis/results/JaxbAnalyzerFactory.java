package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

public class JaxbAnalyzerFactory implements NormalizedTypeAnalyzerFactory {

    @Override
    public NormalizedTypeAnalyzer create(JavaTypeAnalyzer javaTypeAnalyzer) {
        return new JaxbAnalyzer(javaTypeAnalyzer);
    }

}
