package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.collection;

import com.sebastian_daschner.jaxrs_analyzer.model.instructions.NewInstruction;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.ConstPool;

/**
 * Creates a {@link NewInstruction} for a given bytecode position.
 *
 * @author Sebastian Daschner
 */
class NewInstructionBuilder {

    private final CodeIterator codeIterator;
    private final ConstPool pool;

    public NewInstructionBuilder(final CodeIterator codeIterator, final ConstPool pool) {
        this.codeIterator = codeIterator;
        this.pool = pool;
    }

    /**
     * Creates a NEW instruction for the given position.
     *
     * @param position The position
     * @return The instruction
     */
    public NewInstruction build(final int position) {
        final int index = codeIterator.u16bitAt(position + 1);
        final String type = pool.getClassInfo(index);

        return new NewInstruction(type);
    }

}
