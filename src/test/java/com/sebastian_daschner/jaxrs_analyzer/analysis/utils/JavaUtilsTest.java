package com.sebastian_daschner.jaxrs_analyzer.analysis.utils;

import org.junit.Test;

import java.util.List;

public class JavaUtilsTest {

    // TODO
//    @Test
//    public void testDetermineMostSpecificTypeOnlyOne() {
//        final String actualType = JavaUtils.determineMostSpecificType(Types.LIST);
//        assertEquals(Types.LIST, actualType);
//    }
//
//    @Test
//    public void testDetermineMostSpecificTypeParameterized() {
//        final String parameterizedType = "java.util.List<java.lang.String>";
//
//        String actualType = JavaUtils.determineMostSpecificType(Types.LIST, parameterizedType);
//        assertEquals(parameterizedType, actualType);
//
//        actualType = JavaUtils.determineMostSpecificType(parameterizedType, Types.LIST);
//        assertEquals(parameterizedType, actualType);
//    }
//
//    @Test
//    public void testDetermineMostSpecificTypeArray() {
//        final String stringArray = "java.lang.String[]";
//
//        String actualType = JavaUtils.determineMostSpecificType(Types.STRING, stringArray);
//        assertEquals(stringArray, actualType);
//
//        actualType = JavaUtils.determineMostSpecificType(stringArray, Types.STRING);
//        assertEquals(stringArray, actualType);
//    }
//
//    @Test
//    public void testDetermineMostSpecificTypeObject() {
//        final String responseType = "javax.ws.rs.core.Response$Status";
//
//        String actualType = JavaUtils.determineMostSpecificType(Types.OBJECT, responseType);
//        assertEquals(responseType, actualType);
//
//        actualType = JavaUtils.determineMostSpecificType(responseType, Types.OBJECT);
//        assertEquals(responseType, actualType);
//    }
//
//    @Test
//    public void testDetermineMostSpecificTypeInheritance() {
//        final String linkedListType = "java.util.LinkedList";
//
//        String actualType = JavaUtils.determineMostSpecificType(Types.LIST, linkedListType);
//        assertEquals(linkedListType, actualType);
//
//        actualType = JavaUtils.determineMostSpecificType(linkedListType, Types.LIST);
//        assertEquals(linkedListType, actualType);
//    }
//
//    @Test
//    public void testDetermineMostSpecificTypeParameterizedInner() {
//        final String lockList = "java.util.List<java.util.concurrent.locks.Lock>";
//        final String stampedLockList = "java.util.List<java.util.concurrent.locks.ReentrantLock>";
//
//        String actualType = JavaUtils.determineMostSpecificType(lockList, stampedLockList);
//        assertEquals(stampedLockList, actualType);
//
//        actualType = JavaUtils.determineMostSpecificType(stampedLockList, lockList);
//        assertEquals(stampedLockList, actualType);
//    }
//
//    @Test
//    public void testDetermineMostSpecificTypeParameterizedInheritance() {
//        final String lockList = "java.util.List<java.util.concurrent.locks.Lock>";
//        final String lockLinkedList = "java.util.LinkedList<java.util.concurrent.locks.Lock>";
//
//        String actualType = JavaUtils.determineMostSpecificType(lockList, lockLinkedList);
//        assertEquals(lockLinkedList, actualType);
//
//        actualType = JavaUtils.determineMostSpecificType(lockLinkedList, lockList);
//        assertEquals(lockLinkedList, actualType);
//    }
//
//    @Test
//    public void testDetermineMostSpecificTypeParameterizedInnerInheritance() {
//        final String lockList = "java.util.List<java.util.concurrent.locks.Lock>";
//        final String stampedLockLinkedList = "java.util.LinkedList<java.util.concurrent.locks.ReentrantLock>";
//
//        String actualType = JavaUtils.determineMostSpecificType(lockList, stampedLockLinkedList);
//        assertEquals(stampedLockLinkedList, actualType);
//
//        actualType = JavaUtils.determineMostSpecificType(stampedLockLinkedList, lockList);
//        assertEquals(stampedLockLinkedList, actualType);
//    }

}