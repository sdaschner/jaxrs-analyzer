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

package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation;

import com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.MethodHandle;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.*;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.Method;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Simulates the instructions of a method. This class is thread-safe.
 *
 * @author Sebastian Daschner
 */
public class MethodSimulator {

    private final Lock lock = new ReentrantLock();
    private final MethodPool methodPool = MethodPool.getInstance();
    private final Stack<Element> runtimeStack = new Stack<>();

    Map<Integer, Element> localVariables = new HashMap<>();

    private Element returnElement;

    /**
     * Simulates the instructions and collects information about the resource method.
     *
     * @param instructions The instructions of the method
     * @return The return element merged with all possible values
     */
    public Element simulate(final List<Instruction> instructions) {
        lock.lock();
        try {
            returnElement = null;
            return simulateInternal(instructions);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Simulates the instructions of the method.
     *
     * @param instructions The instructions to simulate
     * @return The return element of the method
     */
    Element simulateInternal(final List<Instruction> instructions) {
        instructions.stream().forEach(this::simulate);

        return returnElement;
    }

    /**
     * Simulates the instruction.
     *
     * @param instruction The instruction to simulate
     */
    private void simulate(final Instruction instruction) {
        switch (instruction.getType()) {
            case PUSH:
                Object value = ((PushInstruction) instruction).getValue();
                runtimeStack.push(new Element(JavaUtils.getType(value), value));
                break;
            case METHOD_HANDLE:
                simulateMethodHandle((InvokeDynamicInstruction) instruction);
                break;
            case INVOKE:
                simulateInvoke((InvokeInstruction) instruction);
                break;
            case GET_FIELD:
                runtimeStack.pop();
                runtimeStack.push(new Element(((GetFieldInstruction) instruction).getPropertyType()));
                break;
            case GET_STATIC:
                final GetStaticInstruction getStaticInstruction = (GetStaticInstruction) instruction;
                value = getStaticInstruction.getValue();
                if (value != null)
                    runtimeStack.push(new Element(getStaticInstruction.getPropertyType(), value));
                else
                    runtimeStack.push(new Element(getStaticInstruction.getPropertyType()));
                break;
            case LOAD:
                final LoadInstruction loadInstruction = (LoadInstruction) instruction;
                runtimeStack.push(localVariables.getOrDefault(loadInstruction.getNumber(), new Element(loadInstruction.getVariableType())));
                break;
            case STORE:
                simulateStore((StoreInstruction) instruction);
                break;
            case SIZE_CHANGE:
                simulateSizeChange((SizeChangingInstruction) instruction);
                break;
            case NEW:
                final NewInstruction newInstruction = (NewInstruction) instruction;
                runtimeStack.push(new Element(newInstruction.getCreatedType()));
                break;
            case DUP:
                runtimeStack.push(runtimeStack.peek());
                break;
            case OTHER:
                // do nothing
                break;
            case RETURN:
                mergeReturnElement(runtimeStack.pop());
            case THROW:
                mergePossibleResponse();
                // stack has to be empty for further analysis
                runtimeStack.clear();
                break;
            default:
                throw new IllegalArgumentException("Instruction without type!");
        }
    }

    /**
     * Simulates the invoke dynamic call. Pushes a method handle on the stack.
     *
     * @param instruction The instruction to simulate
     */
    private void simulateMethodHandle(final InvokeDynamicInstruction instruction) {
        final List<Element> arguments = IntStream.range(0, instruction.getDynamicIdentifier().getParameters()).mapToObj(t -> runtimeStack.pop()).collect(Collectors.toList());
        Collections.reverse(arguments);

        if (!instruction.getDynamicIdentifier().isStaticMethod())
            // first parameter is `this`
            arguments.remove(0);

        // adds the transferred arguments of the bootstrap call
        runtimeStack.push(new MethodHandle(instruction.getDynamicIdentifier().getReturnType(), instruction.getIdentifier(), arguments));
    }

    /**
     * Simulates the invoke instruction.
     *
     * @param instruction The instruction to simulate
     */
    private void simulateInvoke(final InvokeInstruction instruction) {
        final List<Element> arguments = new LinkedList<>();
        MethodIdentifier identifier = instruction.getIdentifier();

        IntStream.range(0, identifier.getParameters()).forEach(i -> arguments.add(runtimeStack.pop()));
        Collections.reverse(arguments);

        Element object = null;
        Method method;
        if (!identifier.isStaticMethod()) {
            object = runtimeStack.pop();
            if (object instanceof MethodHandle) {
                method = (Method) object;
            } else {
                method = methodPool.get(identifier);
            }
        } else {
            method = methodPool.get(identifier);
        }
        final Element returnedElement = method.invoke(object, arguments);
        if (returnedElement != null)
            runtimeStack.push(returnedElement);
    }

    /**
     * Simulates the store instruction.
     *
     * @param instruction The instruction to simulate
     */
    private void simulateStore(final StoreInstruction instruction) {
        final int index = instruction.getNumber();
        final Element elementToStore = runtimeStack.pop();

        if (elementToStore instanceof MethodHandle)
            mergeMethodHandleStore(index, (MethodHandle) elementToStore);
        else
            mergeElementStore(index, instruction.getVariableType(), elementToStore);
    }

    /**
     * Merges a stored element to the local variables.
     *
     * @param index        The index of the variable
     * @param variableType The type of the variable
     * @param element      The element to merge
     */
    private void mergeElementStore(final int index, final String variableType, final Element element) {
        final Element created = new Element(variableType);
        created.merge(element);
        localVariables.merge(index, created, Element::merge);
    }

    /**
     * Merges a stored method handle to the local variables.
     *
     * @param index        The index of the variable
     * @param methodHandle The method handle to merge
     */
    private void mergeMethodHandleStore(final int index, final MethodHandle methodHandle) {
        localVariables.merge(index, new MethodHandle(methodHandle), Element::merge);
    }

    /**
     * Checks if the current stack element is eligible for being merged with the returned element.
     */
    private void mergePossibleResponse() {
        // TODO only HttpResponse element?
        if (!runtimeStack.isEmpty() && runtimeStack.peek().getTypes().contains(Types.RESPONSE)) {
            mergeReturnElement(runtimeStack.peek());
        }
    }

    /**
     * Simulates the size change instruction.
     *
     * @param instruction The instruction to simulate
     */
    private void simulateSizeChange(final SizeChangingInstruction instruction) {
        IntStream.range(0, instruction.getNumberOfPops()).forEach(i -> runtimeStack.pop());
        IntStream.range(0, instruction.getNumberOfPushes()).forEach(i -> runtimeStack.push(Element.EMPTY));
    }

    /**
     * Merges the {@code returnElement} with the given element which was popped from the stack.
     * If the {@code returnElement} existed before, the values are merged.
     *
     * @param stackElement The popped element
     */
    private void mergeReturnElement(final Element stackElement) {
        if (returnElement != null)
            stackElement.merge(returnElement);
        returnElement = stackElement;
    }

}
