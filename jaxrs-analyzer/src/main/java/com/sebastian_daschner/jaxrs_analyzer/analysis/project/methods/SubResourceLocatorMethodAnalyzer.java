package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation.MethodPool;
import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation.MethodSimulator;
import com.sebastian_daschner.jaxrs_analyzer.analysis.project.classes.ClassAnalyzer;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.ProjectMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Analyzes sub-resource-locator methods. This class is thread-safe.
 *
 * @author Sebastian Daschner
 */
class SubResourceLocatorMethodAnalyzer extends MethodContentAnalyzer {

    private final Lock lock = new ReentrantLock();
    private final ClassAnalyzer classAnalyzer = new ClassAnalyzer();
    private final MethodSimulator simulator = new MethodSimulator();

    /**
     * Analyzes the sub-resource-locator method as a class result (which will be the content of a method result).
     *
     * @param method      The method
     * @param classResult The class result
     */
    void analyze(final CtMethod method, final ClassResult classResult) {
        lock.lock();
        try {
            buildPackagePrefix(method);

            final String returnType = determineReturnType(method);

            final CtClass subResource = ClassPool.getDefault().get(returnType);
            classAnalyzer.analyzeSubResource(subResource, classResult);
        } catch (NotFoundException e) {
            LogProvider.getLogger().accept("Could not load analyze sub-resource class ");
        } finally {
            lock.unlock();
        }
    }

    /**
     * Determines the return type of the sub-resource-locator by analyzing the bytecode.
     *
     * @return The return type
     */
    private String determineReturnType(final CtMethod method) throws NotFoundException {
        final List<Instruction> visitedInstructions = interpretRelevantInstructions(method);

        // find project defined methods in invoke occurrences
        final Set<ProjectMethod> projectMethods = findProjectMethods(visitedInstructions);

        // add project methods to global method pool
        projectMethods.stream().forEach(MethodPool.getInstance()::addProjectMethod);

        return simulator.simulate(visitedInstructions).getType();
    }

}
