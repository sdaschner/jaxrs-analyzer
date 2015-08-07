package com.sebastian_daschner.jaxrs_analyzer.analysis.utils;

import com.sebastian_daschner.jaxrs_analyzer.model.types.Type;
import com.sebastian_daschner.jaxrs_analyzer.model.types.Types;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JavaUtilsTest {

    @Test
    public void testDetermineMostSpecificTypeOnlyOne() {
        final Type actualType = JavaUtils.determineMostSpecificType(Types.LIST);
        assertEquals(Types.LIST, actualType);
    }

    @Test
    public void testDetermineMostSpecificTypeParameterized() {
        final Type parameterizedType = new Type("java.util.List<java.lang.String>");

        Type actualType = JavaUtils.determineMostSpecificType(Types.LIST, parameterizedType);
        assertEquals(parameterizedType, actualType);

        actualType = JavaUtils.determineMostSpecificType(parameterizedType, Types.LIST);
        assertEquals(parameterizedType, actualType);
    }

    @Test
    public void testDetermineMostSpecificTypeArray() {
        final Type stringArray = new Type("java.lang.String[]");

        Type actualType = JavaUtils.determineMostSpecificType(Types.STRING, stringArray);
        assertEquals(stringArray, actualType);

        actualType = JavaUtils.determineMostSpecificType(stringArray, Types.STRING);
        assertEquals(stringArray, actualType);
    }

    @Test
    public void testDetermineMostSpecificTypeObject() {
        final Type responseType = new Type("javax.ws.rs.core.Response$Status");

        Type actualType = JavaUtils.determineMostSpecificType(Types.OBJECT, responseType);
        assertEquals(responseType, actualType);

        actualType = JavaUtils.determineMostSpecificType(responseType, Types.OBJECT);
        assertEquals(responseType, actualType);
    }

    @Test
    public void testDetermineMostSpecificTypeInheritance() {
        final Type linkedListType = new Type("java.util.LinkedList");

        Type actualType = JavaUtils.determineMostSpecificType(Types.LIST, linkedListType);
        assertEquals(linkedListType, actualType);

        actualType = JavaUtils.determineMostSpecificType(linkedListType, Types.LIST);
        assertEquals(linkedListType, actualType);
    }

    @Test
    public void testDetermineMostSpecificTypeParameterizedInner() {
        final Type lockList = new Type("java.util.List<java.util.concurrent.locks.Lock>");
        final Type stampedLockList = new Type("java.util.List<java.util.concurrent.locks.ReentrantLock>");

        Type actualType = JavaUtils.determineMostSpecificType(lockList, stampedLockList);
        assertEquals(stampedLockList, actualType);

        actualType = JavaUtils.determineMostSpecificType(stampedLockList, lockList);
        assertEquals(stampedLockList, actualType);
    }

    @Test
    public void testDetermineMostSpecificTypeParameterizedInheritance() {
        final Type lockList = new Type("java.util.List<java.util.concurrent.locks.Lock>");
        final Type lockLinkedList = new Type("java.util.LinkedList<java.util.concurrent.locks.Lock>");

        Type actualType = JavaUtils.determineMostSpecificType(lockList, lockLinkedList);
        assertEquals(lockLinkedList, actualType);

        actualType = JavaUtils.determineMostSpecificType(lockLinkedList, lockList);
        assertEquals(lockLinkedList, actualType);
    }

    @Test
    public void testDetermineMostSpecificTypeParameterizedInnerInheritance() {
        final Type lockList = new Type("java.util.List<java.util.concurrent.locks.Lock>");
        final Type stampedLockLinkedList = new Type("java.util.LinkedList<java.util.concurrent.locks.ReentrantLock>");

        Type actualType = JavaUtils.determineMostSpecificType(lockList, stampedLockLinkedList);
        assertEquals(stampedLockLinkedList, actualType);

        actualType = JavaUtils.determineMostSpecificType(stampedLockLinkedList, lockList);
        assertEquals(stampedLockLinkedList, actualType);
    }

}