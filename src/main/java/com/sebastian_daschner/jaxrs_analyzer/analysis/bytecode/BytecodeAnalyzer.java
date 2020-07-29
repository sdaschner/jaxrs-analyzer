package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode;

import com.sebastian_daschner.jaxrs_analyzer.analysis.JobRegistry;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

/**
 * @author Sebastian Daschner
 */
public class BytecodeAnalyzer {

    private final ResourceMethodContentAnalyzer methodContentAnalyzer = new ResourceMethodContentAnalyzer();
    private final SubResourceLocatorMethodContentAnalyzer subResourceLocatorAnalyzer;

	public BytecodeAnalyzer() {
		this(new JobRegistry());
	}

	public BytecodeAnalyzer(JobRegistry jobRegistry) {
		subResourceLocatorAnalyzer = new SubResourceLocatorMethodContentAnalyzer(jobRegistry);
	}

	/**
     * Analyzes the bytecode instructions of the method results and interprets JAX-RS relevant information.
     */
    public void analyzeBytecode(final ClassResult classResult) {
        classResult.getMethods().forEach(this::analyzeBytecode);
    }

    private void analyzeBytecode(final MethodResult methodResult) {
        if (methodResult.getHttpMethod() == null) {
            // sub-resource
            subResourceLocatorAnalyzer.analyze(methodResult);
        } else {
            methodContentAnalyzer.analyze(methodResult);
        }
    }

}
