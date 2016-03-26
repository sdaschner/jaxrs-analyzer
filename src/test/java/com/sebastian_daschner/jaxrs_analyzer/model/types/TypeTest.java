package com.sebastian_daschner.jaxrs_analyzer.model.types;

/**
 * @author Sebastian Daschner
 */
public class TypeTest {

    // TODO
//    @Test
//    public void testSimple() {
//        final Type type = new Type("java.lang.String");
//        assertThat(type.getCtClass().getName(), is("java.lang.String"));
//        assertThat(type.toString(), is("java.lang.String"));
//        assertThat(type.getTypeParameters().size(), is(0));
//    }
//
//    @Test
//    public void testSimpleMap() {
//        final Type type = new Type("java.util.Map");
//        assertThat(type.getCtClass().getName(), is("java.util.Map"));
//        assertThat(type.toString(), is("java.util.Map"));
//        assertThat(type.getTypeParameters().size(), is(0));
//        assertTrue(type.isAssignableTo(Types.MAP));
//    }
//
//    @Test
//    public void testParameterizedMap() {
//        final Type type = new Type("java.util.Map<java.lang.String,java.lang.String>");
//        assertThat(type.getCtClass().getName(), is("java.util.Map"));
//        assertThat(type.toString(), is("java.util.Map<java.lang.String, java.lang.String>"));
//        assertThat(type.getTypeParameters().size(), is(2));
//        assertThat(type.getTypeParameters().get(0).getCtClass().getName(), is("java.lang.String"));
//        assertThat(type.getTypeParameters().get(0).toString(), is("java.lang.String"));
//        assertThat(type.getTypeParameters().get(1).getCtClass().getName(), is("java.lang.String"));
//        assertThat(type.getTypeParameters().get(1).toString(), is("java.lang.String"));
//        assertTrue(type.isAssignableTo(Types.MAP));
//    }
//
//    @Test
//    public void testNestedSimpleClass() {
//        final Type type = new Type("java.util.Map$Entry");
//        assertThat(type.getCtClass().getName(), is("java.util.Map$Entry"));
//        assertThat(type.toString(), is("java.util.Map$Entry"));
//        assertThat(type.getTypeParameters().size(), is(0));
//        assertTrue(!type.isAssignableTo(Types.MAP));
//    }
//
//    @Test
//    public void testNestedParameterizedClass() {
//        final Type type = new Type("java.util.Map$Entry<java.lang.String,java.lang.String>");
//        assertThat(type.getCtClass().getName(), is("java.util.Map$Entry"));
//        assertThat(type.toString(), is("java.util.Map$Entry<java.lang.String, java.lang.String>"));
//        assertThat(type.getTypeParameters().size(), is(2));
//        assertThat(type.getTypeParameters().get(0).getCtClass().getName(), is("java.lang.String"));
//        assertThat(type.getTypeParameters().get(0).toString(), is("java.lang.String"));
//        assertThat(type.getTypeParameters().get(1).getCtClass().getName(), is("java.lang.String"));
//        assertThat(type.getTypeParameters().get(1).toString(), is("java.lang.String"));
//        assertTrue(!type.isAssignableTo(Types.MAP));
//    }
//
//    @Test
//    public void testPrimitiveTypes() {
//        final Type type = new Type("int");
//        assertThat(type.getCtClass().getName(), is("int"));
//        assertThat(type.toString(), is("int"));
//        assertThat(type.getTypeParameters().size(), is(0));
//        assertTrue(type.equals(Types.PRIMITIVE_INT));
//    }
//
//    @Test
//    public void testSimpleArrays() {
//        final Type type = new Type("java.lang.String[]");
//        assertThat(type.getCtClass().getName(), is("java.lang.String[]"));
//        assertThat(type.toString(), is("java.lang.String[]"));
//        assertThat(type.getTypeParameters().size(), is(0));
//        assertTrue(!type.equals(Types.STRING));
//    }
//
//    @Test
//    public void testMultiDimensionalArrays() {
//        final Type type = new Type("java.lang.String[][]");
//        assertThat(type.getCtClass().getName(), is("java.lang.String[][]"));
//        assertThat(type.toString(), is("java.lang.String[][]"));
//        assertThat(type.getTypeParameters().size(), is(0));
//        assertTrue(!type.equals(Types.STRING));
//    }
//
//    @Test
//    public void testParameterizedTypesInArrays() {
//        final Type type = new Type("java.util.Collection<java.lang.String>[]");
//        assertThat(type.getCtClass().getName(), is("java.util.Collection[]"));
//        assertThat(type.toString(), is("java.util.Collection<java.lang.String>[]"));
//        assertThat(type.getTypeParameters().size(), is(1));
//        assertThat(type.getTypeParameters().get(0).getCtClass().getName(), is("java.lang.String"));
//        assertTrue(!type.equals(Types.STRING));
//    }
//
//    @Test
//    public void testParameterizedTypesInList() {
//        final Type type = new Type("java.util.List<java.lang.Long>");
//        assertThat(type.getCtClass().getName(), is("java.util.List"));
//        assertThat(type.toString(), is("java.util.List<java.lang.Long>"));
//        assertThat(type.getTypeParameters().size(), is(1));
//        assertThat(type.getTypeParameters().get(0).getCtClass().getName(), is("java.lang.Long"));
//        assertTrue(!type.equals(Types.STRING));
//    }
//
//    @Test
//    public void testParameterizedTypesInMultiDimensionalArrays() {
//        final Type type = new Type("java.util.Collection<java.lang.String>[][]");
//        assertThat(type.getCtClass().getName(), is("java.util.Collection[][]"));
//        assertThat(type.toString(), is("java.util.Collection<java.lang.String>[][]"));
//        assertThat(type.getTypeParameters().size(), is(1));
//        assertThat(type.getTypeParameters().get(0).getCtClass().getName(), is("java.lang.String"));
//        assertTrue(!type.equals(Types.STRING));
//    }
//
//    @Test
//    public void testPrivateInterface() {
//        final Type type = new Type("com.sebastian_daschner.jaxrs_analyzer.model.Types.TypeTest$ConfigurationManager$Configuration");
//        assertThat(type.getCtClass().getName(), is("com.sebastian_daschner.jaxrs_analyzer.model.Types.TypeTest$ConfigurationManager$Configuration"));
//        assertThat(type.toString(), is("com.sebastian_daschner.jaxrs_analyzer.model.Types.TypeTest$ConfigurationManager$Configuration"));
//        assertThat(type.getTypeParameters().size(), is(0));
//    }
//
//    private interface ConfigurationManager {
//        Configuration getConfiguration(String name);
//
//        class Configuration {
//        }
//    }

}