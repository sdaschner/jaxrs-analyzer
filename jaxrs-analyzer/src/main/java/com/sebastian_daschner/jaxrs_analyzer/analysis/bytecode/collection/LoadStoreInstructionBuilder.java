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

package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection;

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.LoadStoreInstruction;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.StoreInstruction;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.LoadInstruction;
import javassist.bytecode.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Creates the {@link LoadStoreInstruction} for a given byte code position.
 *
 * @author Sebastian Daschner
 */
class LoadStoreInstructionBuilder {

    /**
     * The variable name which is taken, if no information is found in the local variables attribute.
     */
    private static final String UNKNOWN_VARIABLE_NAME_PREFIX = "variable$";
    private final Map<Integer, String> variableNames;
    private final Map<Integer, String> variableTypes;

    LoadStoreInstructionBuilder(final CodeAttribute codeAttribute) {
        final LocalVariableAttribute localVariableAttribute = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        final LocalVariableTypeAttribute localVariableTypeAttribute = (LocalVariableTypeAttribute) codeAttribute.getAttribute(LocalVariableAttribute.typeTag);

        variableNames = buildVariableNames(localVariableAttribute);
        variableTypes = buildVariableTypes(localVariableAttribute, localVariableTypeAttribute);
    }

    /**
     * Creates the variable names.
     *
     * @param variableAttribute The localVariableTable attribute
     * @return The variable names for the variable indexes
     */
    private Map<Integer, String> buildVariableNames(final LocalVariableAttribute variableAttribute) {
        return IntStream.range(0, variableAttribute.tableLength())
                .collect(HashMap::new,
                        (m, i) -> m.put(variableAttribute.index(i), variableAttribute.variableName(i))
                        , Map::putAll);
    }

    /**
     * Creates the variable types.
     *
     * @param variableAttribute     The localVariableTable attribute
     * @param variableTypeAttribute The localVariableTypeTable attribute
     * @return The variable types for the variable indexes
     */
    private Map<Integer, String> buildVariableTypes(final LocalVariableAttribute variableAttribute,
                                                    final LocalVariableTypeAttribute variableTypeAttribute) {
        final Map<Integer, String> types = IntStream.range(0, variableAttribute.tableLength())
                .collect(HashMap::new,
                        (m, i) -> m.put(variableAttribute.index(i), getType(variableAttribute, i))
                        , Map::putAll);

        if (variableTypeAttribute != null) {
            IntStream.range(0, variableTypeAttribute.tableLength())
                    .forEach(i -> types.put(variableTypeAttribute.index(i), getType(variableTypeAttribute, i)));
        }

        return types;
    }

    /**
     * Returns the variable type for the given index in the given table.
     *
     * @param variableAttribute The table attribute to use
     * @param index             The variable index
     * @return The type
     */
    private String getType(final LocalVariableAttribute variableAttribute, final int index) {
        final String signature = variableAttribute.signature(index);
        try {
            return SignatureAttribute.toTypeSignature(signature).toString();
        } catch (BadBytecode e) {
            throw new IllegalStateException("Could not analyze bytecode", e);
        }
    }

    /**
     * Creates a {@link LoadInstruction} for the given index.
     *
     * @param index The variable index
     * @return The instruction
     */
    LoadInstruction buildLoad(final int index) {
        final String type = variableTypes.getOrDefault(index, Object.class.getCanonicalName());
        final String name = variableNames.getOrDefault(index, UNKNOWN_VARIABLE_NAME_PREFIX + index);
        return new LoadInstruction(index, type, name);
    }

    /**
     * Creates a {@link StoreInstruction} for the given index.
     *
     * @param index The variable index
     * @return The instruction
     */
    StoreInstruction buildStore(final int index) {
        final String type = variableTypes.getOrDefault(index, Object.class.getCanonicalName());
        final String name = variableNames.getOrDefault(index, UNKNOWN_VARIABLE_NAME_PREFIX + index);
        return new StoreInstruction(index, type, name);
    }

}
