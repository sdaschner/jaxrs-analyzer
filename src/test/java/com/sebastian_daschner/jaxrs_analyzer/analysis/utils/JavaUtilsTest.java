package com.sebastian_daschner.jaxrs_analyzer.analysis.utils;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import org.junit.Test;

import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.*;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class JavaUtilsTest {

    @Test
    public void testGetTypeParameters() {
        assertThat(getTypeParameters("B").size(), is(0));
        assertThat(getTypeParameters("Z").size(), is(0));
        assertThat(getTypeParameters(Types.STRING).size(), is(0));
        assertThat(getTypeParameters("[Ljava/lang/String;").size(), is(0));
        assertThat(getTypeParameters("Ljava/util/List<Ljava/lang/String;>;"), hasItems(Types.STRING));
        assertThat(getTypeParameters("Ljava/lang/Map<Ljava/lang/String;Ljava/lang/Integer;>;"), hasItems(Types.STRING, Types.INTEGER));
        assertThat(getTypeParameters("Ljava/util/List<Ljava/util/List<Ljava/lang/Integer;>;>;"), hasItems("Ljava/util/List<Ljava/lang/Integer;>;"));
        assertThat(getTypeParameters("Ljava/util/Map<Ljava/util/List<Ljava/lang/Integer;>;Ljava/util/List<Ljava/lang/String;>;>;"), hasItems("Ljava/util/List<Ljava/lang/Integer;>;", "Ljava/util/List<Ljava/lang/String;>;"));
    }

    @Test
    public void testIsAssignableToSame() {
        final String sameType = "Ljava/lang/Object;";
        assertTrue(isAssignableTo(sameType, Types.OBJECT));
        assertTrue(isAssignableTo(Types.OBJECT, sameType));
    }

    @Test
    public void testIsAssignableToPrimitives() {
        assertFalse(isAssignableTo(Types.PRIMITIVE_INT, Types.OBJECT));
        assertFalse(isAssignableTo(Types.OBJECT, Types.PRIMITIVE_INT));
        assertFalse(isAssignableTo(Types.PRIMITIVE_BOOLEAN, Types.PRIMITIVE_INT));
        assertTrue(isAssignableTo(Types.PRIMITIVE_INT, Types.PRIMITIVE_INT));
    }

    @Test
    public void testIsAssignableToArray() {
        final String intArray = "[I";
        final String objectArray = "[Ljava/lang/Object;";
        final String listArray = "[Ljava/util/List;";
        final String stringListArray = "[Ljava/util/List<Ljava/lang/String;>";
        final String linkedListArray = "[Ljava/util/LinkedList;";

        assertFalse(isAssignableTo(intArray, Types.PRIMITIVE_INT));
        assertFalse(isAssignableTo(Types.PRIMITIVE_INT, intArray));
        assertFalse(isAssignableTo(objectArray, intArray));
        assertFalse(isAssignableTo(intArray, objectArray));
        assertTrue(isAssignableTo(objectArray, objectArray));
        assertTrue(isAssignableTo(intArray, intArray));
        assertTrue(isAssignableTo(listArray, stringListArray));
        assertTrue(isAssignableTo(stringListArray, listArray));
        assertTrue(isAssignableTo(linkedListArray, listArray));
        assertFalse(isAssignableTo(listArray, linkedListArray));
    }

    @Test
    public void testIsAssignableToInheritance() {
        final String parentType = "Ljava/lang/Number;";
        final String inheritedType = "Ljava/lang/Integer;";
        assertFalse(isAssignableTo(parentType, inheritedType));
        assertTrue(isAssignableTo(inheritedType, parentType));
    }

    @Test
    public void testIsAssignableToGeneric() {
        final String parentType = "Ljava/util/List;";
        final String inheritedType = "Ljava/util/LinkedList;";
        assertFalse(isAssignableTo(parentType, inheritedType));
        assertTrue(isAssignableTo(inheritedType, parentType));
    }

    @Test
    public void testIsAssignableToParameterized() {
        final String parentType = "Ljava/util/List<Ljava/lang/String;>;";
        final String inheritedType = "Ljava/util/LinkedList<Ljava/lang/String;>;";
        assertFalse(isAssignableTo(parentType, inheritedType));
        assertTrue(isAssignableTo(inheritedType, parentType));
    }

    @Test
    public void testIsAssignableToDifferentTypes() {
        assertFalse(isAssignableTo(Types.STRING, Types.STREAM));
        assertFalse(isAssignableTo(Types.STREAM, Types.STRING));
    }

    @Test
    public void testIsAssignableToParameterizedInheritedParameters() {
        final String parentType = "Ljava/util/List<Ljava/lang/Number;>;";
        final String inheritedType = "Ljava/util/List<Ljava/lang/Integer;>;";
        assertFalse(isAssignableTo(parentType, inheritedType));
        assertFalse(isAssignableTo(inheritedType, parentType));
    }

    @Test
    public void testIsAssignableToParameterizedInheritedParametersExtended() {
        final String parentType = "Ljava/util/List<Ljava/lang/Number;>;";
        final String inheritedType = "Ljava/util/LinkedList<Ljava/lang/Integer;>;";
        assertFalse(isAssignableTo(parentType, inheritedType));
        assertFalse(isAssignableTo(inheritedType, parentType));
    }

    @Test
    public void testDetermineMostSpecificTypeOnlyOne() {
        final String actualType = determineMostSpecificType(Types.LIST);
        assertEquals(Types.LIST, actualType);
    }

    @Test
    public void testDetermineMostSpecificTypeParameterized() {
        final String parameterizedType = "Ljava/util/List<Ljava/lang/String;>;";

        String actualType = determineMostSpecificType(Types.LIST, parameterizedType);
        assertEquals(parameterizedType, actualType);

        actualType = determineMostSpecificType(parameterizedType, Types.LIST);
        assertEquals(parameterizedType, actualType);
    }

    @Test
    public void testDetermineMostSpecificTypeArray() {
        final String stringArray = "[Ljava/lang/String;";

        String actualType = determineMostSpecificType(Types.STRING, stringArray);
        assertEquals(stringArray, actualType);

        actualType = determineMostSpecificType(stringArray, Types.STRING);
        assertEquals(stringArray, actualType);
    }

    @Test
    public void testDetermineMostSpecificTypeObject() {
        final String responseType = "Ljavax/ws/rs/core/Response$Status;";

        String actualType = determineMostSpecificType(Types.OBJECT, responseType);
        assertEquals(responseType, actualType);

        actualType = determineMostSpecificType(responseType, Types.OBJECT);
        assertEquals(responseType, actualType);
    }

    @Test
    public void testDetermineMostSpecificTypeInheritance() {
        final String linkedListType = "Ljava/util/LinkedList;";

        String actualType = determineMostSpecificType(Types.LIST, linkedListType);
        assertEquals(linkedListType, actualType);

        actualType = determineMostSpecificType(linkedListType, Types.LIST);
        assertEquals(linkedListType, actualType);
    }

    @Test
    public void testDetermineMostSpecificTypeParameterizedInner() {
        final String lockList = "Ljava/util/List<Ljava/util/concurrent/locks/Lock;>;";
        final String stampedLockList = "Ljava/util/List<Ljava/util/concurrent/locks/ReentrantLock;>;";

        String actualType = determineMostSpecificType(lockList, stampedLockList);
        assertEquals(stampedLockList, actualType);

        actualType = determineMostSpecificType(stampedLockList, lockList);
        assertEquals(stampedLockList, actualType);
    }

    @Test
    public void testDetermineMostSpecificTypeParameterizedInheritance() {
        final String lockList = "Ljava/util/List<Ljava/util/concurrent/locks/Lock;>;";
        final String lockLinkedList = "Ljava/util/LinkedList<Ljava/util/concurrent/locks/Lock;>;";

        String actualType = determineMostSpecificType(lockList, lockLinkedList);
        assertEquals(lockLinkedList, actualType);

        actualType = determineMostSpecificType(lockLinkedList, lockList);
        assertEquals(lockLinkedList, actualType);
    }

    @Test
    public void testDetermineMostSpecificTypeParameterizedInnerInheritance() {
        final String lockList = "Ljava/util/List<Ljava/util/concurrent/locks/Lock;>;";
        final String stampedLockLinkedList = "Ljava/util/LinkedList<Ljava/util/concurrent/locks/ReentrantLock;>;";

        String actualType = determineMostSpecificType(lockList, stampedLockLinkedList);
        assertEquals(stampedLockLinkedList, actualType);

        actualType = determineMostSpecificType(stampedLockLinkedList, lockList);
        assertEquals(stampedLockLinkedList, actualType);
    }

}