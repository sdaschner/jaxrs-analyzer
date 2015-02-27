package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection;

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.GetFieldInstruction;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.GetPropertyInstruction;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.GetStaticInstruction;
import javassist.bytecode.BadBytecode;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;
import javassist.bytecode.SignatureAttribute;

/**
 * Creates the {@link GetPropertyInstruction} for a given byte code position.
 *
 * @author Sebastian Daschner
 */
class FieldInstructionBuilder {

    private final CodeIterator codeIterator;
    private final ConstPool pool;

    FieldInstructionBuilder(final CodeIterator codeIterator, final ConstPool pool) {
        this.codeIterator = codeIterator;
        this.pool = pool;
    }

    /**
     * Creates a {@link GetFieldInstruction}.
     *
     * @param position The bytecode position
     * @return The instruction
     * @throws BadBytecode If the bytecode could not be analyzed
     */
    GetFieldInstruction buildGetField(final int position) throws BadBytecode {
        final int index = codeIterator.u16bitAt(position + 1);

        final String className = pool.getFieldrefClassName(index);
        final String fieldName = pool.getFieldrefName(index);
        final String fieldType = getFieldType(pool.getFieldrefType(index));

        return new GetFieldInstruction(className, fieldName, fieldType);
    }

    /**
     * Creates a {@link GetStaticInstruction}.
     *
     * @param position The bytecode position
     * @return The instruction
     * @throws BadBytecode If the bytecode could not be analyzed
     */
    GetStaticInstruction buildGetStatic(final int position) throws BadBytecode {
        final int index = codeIterator.u16bitAt(position + 1);

        final String className = pool.getFieldrefClassName(index);
        final String fieldName = pool.getFieldrefName(index);
        final String fieldType = getFieldType(pool.getFieldrefType(index));

        return new GetStaticInstruction(className, fieldName, fieldType);
    }

    /**
     * Returns the signature field type to a Java type.
     *
     * @param sigFieldType The field type
     * @return The Java type
     * @throws BadBytecode If the bytecode could not be analyzed
     */
    private String getFieldType(final String sigFieldType) throws BadBytecode {
        final SignatureAttribute.Type type = SignatureAttribute.toTypeSignature(sigFieldType);
        return type.toString();
    }

}
