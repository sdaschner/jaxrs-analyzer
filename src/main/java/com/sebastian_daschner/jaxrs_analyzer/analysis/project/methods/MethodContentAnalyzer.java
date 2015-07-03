/*
 * Copyright (C) 2015 Sebastian Daschner, sebastian-daschner.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sebastian_daschner.jaxrs_analyzer.analysis.project.methods;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection.ByteCodeCollector;
import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.reduction.RelevantInstructionReducer;
import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.InvokeInstruction;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.ProjectMethod;
import javassist.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Analyzes the content of a method. Sub classes have to be thread-safe.
 *
 * @author Sebastian Daschner
 */
abstract class MethodContentAnalyzer {

    /**
     * The number of package hierarchies which are taken to identify project resources.
     */
    private static final int PROJECT_PACKAGE_HIERARCHIES = 2;
    private final ByteCodeCollector byteCodeCollector = new ByteCodeCollector();
    private final RelevantInstructionReducer instructionReducer = new RelevantInstructionReducer();
    protected String projectPackagePrefix;

    /**
     * Interprets the relevant instructions for the given method.
     *
     * @param method The method to interpret
     * @return The instructions
     */
    protected List<Instruction> interpretRelevantInstructions(final CtBehavior method) {
        final List<Instruction> allInstructions = byteCodeCollector.buildInstructions(method);
        return instructionReducer.reduceInstructions(allInstructions);
    }

    /**
     * Builds the project package prefix for the class of given method.
     * The current project which is analyzed is identified by the first two package nodes.
     *
     * @param method The method
     */
    protected void buildPackagePrefix(final CtMethod method) {
        final String packageName = method.getDeclaringClass().getPackageName();
        final String[] splitPackage = packageName.split("\\.");

        if (splitPackage.length >= PROJECT_PACKAGE_HIERARCHIES) {
            projectPackagePrefix = String.join(".", splitPackage[0], splitPackage[1]);
        } else {
            projectPackagePrefix = packageName;
        }
    }

    /**
     * Searches for own project method invoke instructions in the given list.
     *
     * @param instructions The instructions where to search
     * @return The found project methods
     */
    protected Set<ProjectMethod> findProjectMethods(final List<Instruction> instructions) {
        final Set<ProjectMethod> projectMethods = new HashSet<>();

        addProjectMethods(instructions, projectMethods);

        return projectMethods;
    }

    /**
     * Adds all project methods called in the given {@code instructions} to the {@code projectMethods} recursively.
     *
     * @param instructions   The instructions of the current method
     * @param projectMethods All found project methods
     */
    private void addProjectMethods(final List<Instruction> instructions, final Set<ProjectMethod> projectMethods) {
        Set<MethodIdentifier> projectMethodIdentifiers = findUnhandledProjectMethodIdentifiers(instructions, projectMethods);

        for (MethodIdentifier identifier : projectMethodIdentifiers) {
            final CtBehavior method;
            try {
                method = getMethod(identifier);
            } catch (NotFoundException e) {
                LogProvider.error("Could not interpret project method: " + identifier);
                LogProvider.debug(e);
                continue;
            }

            final int modifiers = method.getModifiers();
            if (Modifier.isNative(modifiers) || Modifier.isAbstract(modifiers))
                continue;

            final List<Instruction> nestedMethodInstructions = interpretRelevantInstructions(method);
            projectMethods.add(new ProjectMethod(identifier, nestedMethodInstructions));
            addProjectMethods(nestedMethodInstructions, projectMethods);
        }
    }

    /**
     * Returns project method identifiers of invoke instructions which are not included in the {@code projectMethods}.
     *
     * @param instructions   The instructions of the current method
     * @param projectMethods All found project methods
     * @return The new method identifiers of unhandled project method invoke instructions
     */
    private Set<MethodIdentifier> findUnhandledProjectMethodIdentifiers(final List<Instruction> instructions, final Set<ProjectMethod> projectMethods) {
        // find own methods
        return instructions.stream().filter(i -> i.getType() == Instruction.Type.INVOKE || i.getType() == Instruction.Type.METHOD_HANDLE)
                .map(i -> (InvokeInstruction) i).filter(this::isProjectMethod).map(InvokeInstruction::getIdentifier)
                .filter(i -> projectMethods.stream().noneMatch(m -> m.matches(i)))
                .collect(Collectors.toSet());
    }

    /**
     * Returns the method (Javassist {@link CtBehavior}) for the given method or constructor identifier.
     *
     * @param identifier The method identifier
     * @return The Javassist behavior
     * @throws NotFoundException If the method could not be analyzed
     */
    // TODO refactor to central utils
    private CtBehavior getMethod(final MethodIdentifier identifier) throws NotFoundException {
        final CtClass[] parameterClasses = JavaUtils.getParameterClasses(identifier);
        final CtClass ctClass = ClassPool.getDefault().get(identifier.getClassName());

        if (JavaUtils.isInitializerName(identifier.getMethodName())) {
            return ctClass.getDeclaredConstructor(parameterClasses);
        }
        return ctClass.getDeclaredMethod(identifier.getMethodName(), parameterClasses);
    }

    /**
     * Checks if the given instruction invokes a method defined in the analyzed project.
     *
     * @param instruction The invoke instruction
     * @return {@code true} if method was defined in the project
     */
    private boolean isProjectMethod(final InvokeInstruction instruction) {
        final MethodIdentifier identifier = instruction.getIdentifier();

        // check if method is in own package
        return identifier.getClassName().startsWith(projectPackagePrefix);
    }

}
