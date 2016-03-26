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

package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.reduction.RelevantInstructionReducer;
import com.sebastian_daschner.jaxrs_analyzer.analysis.classes.ProjectMethodClassVisitor;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.InvokeInstruction;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.ProjectMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private final RelevantInstructionReducer instructionReducer = new RelevantInstructionReducer();
    private String projectPackagePrefix;

    /**
     * Interprets the relevant instructions for the given method.
     *
     * @param instructions The instructions to reduce
     * @return The reduced instructions
     */
    List<Instruction> interpretRelevantInstructions(final List<Instruction> instructions) {
        return instructionReducer.reduceInstructions(instructions);
    }

    /**
     * Builds the project package prefix for the class of given method.
     * The current project which is analyzed is identified by the first two package nodes.
     */
    void buildPackagePrefix(final String className) {
        // TODO test
        final int lastPackageSeparator = className.lastIndexOf('/');
        final String packageName = className.substring(0, lastPackageSeparator == -1 ? className.length() : lastPackageSeparator);
        final String[] splitPackage = packageName.split("/");

        if (splitPackage.length >= PROJECT_PACKAGE_HIERARCHIES) {
            projectPackagePrefix = IntStream.range(0, PROJECT_PACKAGE_HIERARCHIES).mapToObj(i -> splitPackage[i]).collect(Collectors.joining("."));
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
    Set<ProjectMethod> findProjectMethods(final List<Instruction> instructions) {
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
            // TODO cache results -> singleton pool?

            final MethodResult methodResult = visitProjectMethod(identifier);
            if (methodResult == null) {
                continue;
            }

            final List<Instruction> nestedMethodInstructions = interpretRelevantInstructions(methodResult.getInstructions());
            projectMethods.add(new ProjectMethod(identifier, nestedMethodInstructions));
            addProjectMethods(nestedMethodInstructions, projectMethods);
        }
    }

    private MethodResult visitProjectMethod(MethodIdentifier identifier) {
        try {
            final ClassReader classReader = new ClassReader(identifier.getContainingClass());
            final MethodResult methodResult = new MethodResult();
            methodResult.setOriginalMethodSignature(identifier.getSignature());
            final ClassVisitor visitor = new ProjectMethodClassVisitor(methodResult, identifier);

            classReader.accept(visitor, ClassReader.EXPAND_FRAMES);
            return methodResult;
        } catch (IOException e) {
            LogProvider.error("Could not analyze project method " + identifier.getContainingClass() + "#" + identifier.getMethodName());
            LogProvider.debug(e);
            return null;
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
        return instructions.stream().filter(i -> i.getType() == Instruction.InstructionType.INVOKE || i.getType() == Instruction.InstructionType.METHOD_HANDLE)
                .map(i -> (InvokeInstruction) i).filter(this::isProjectMethod).map(InvokeInstruction::getIdentifier)
                .filter(i -> projectMethods.stream().noneMatch(m -> m.matches(i)))
                .collect(Collectors.toSet());
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
        return identifier.getContainingClass().startsWith(projectPackagePrefix);
    }

}
