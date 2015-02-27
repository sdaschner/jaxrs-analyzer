package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection;

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.PushInstruction;
import javassist.bytecode.ConstPool;

/**
 * Creates the {@link PushInstruction} for a given byte code position.
 *
 * @author Sebastian Daschner
 */
class LdcPushInstructionBuilder {

    private final ConstPool pool;

    LdcPushInstructionBuilder(final ConstPool pool) {
        this.pool = pool;
    }

    /**
     * Creates an LDC push instruction for the given const pool index.
     *
     * @param index The index in the const pool
     * @return The push instruction
     */
    PushInstruction build(final int index) {
        return new PushInstruction(getLdc(index));
    }

    /**
     * Returns the LDC object for the given index.
     *
     * @param index The index in the const pool
     * @return The LDC object
     */
    private Object getLdc(final int index) {
        int tag = pool.getTag(index);
        switch (tag) {
            case ConstPool.CONST_String:
                return pool.getStringInfo(index);
            case ConstPool.CONST_Integer:
                return pool.getIntegerInfo(index);
            case ConstPool.CONST_Float:
                return pool.getFloatInfo(index);
            case ConstPool.CONST_Long:
                return pool.getLongInfo(index);
            case ConstPool.CONST_Double:
                return pool.getDoubleInfo(index);
            case ConstPool.CONST_Class:
                // classes are pushed as string
                return pool.getClassInfo(index);
            default:
                throw new IllegalStateException("Unknown LDC instruction.");
        }
    }

}
