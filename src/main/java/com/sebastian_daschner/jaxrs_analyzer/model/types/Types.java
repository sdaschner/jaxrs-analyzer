package com.sebastian_daschner.jaxrs_analyzer.model.types;

import javax.json.*;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * All needed known Java / JavaEE types.
 *
 * @author Sebastian Daschner
 */
public abstract class Types {

    public static final Type PRIMITIVE_VOID = new Type(void.class.getName());
    public static final Type BOOLEAN = new Type(Boolean.class.getName());
    public static final Type PRIMITIVE_BOOLEAN = new Type(boolean.class.getName());
    public static final Type CHARACTER = new Type(Character.class.getName());
    public static final Type PRIMITIVE_CHAR = new Type(char.class.getName());
    public static final Type INTEGER = new Type(Integer.class.getName());
    public static final Type PRIMITIVE_INT = new Type(int.class.getName());
    public static final Type BYTE = new Type(Byte.class.getName());
    public static final Type PRIMITIVE_BYTE = new Type(byte.class.getName());
    public static final Type SHORT = new Type(Short.class.getName());
    public static final Type PRIMITIVE_SHORT = new Type(short.class.getName());
    public static final Type DOUBLE = new Type(Double.class.getName());
    public static final Type PRIMITIVE_DOUBLE = new Type(double.class.getName());
    public static final Type FLOAT = new Type(Float.class.getName());
    public static final Type PRIMITIVE_FLOAT = new Type(float.class.getName());
    public static final Type LONG = new Type(Long.class.getName());
    public static final Type PRIMITIVE_LONG = new Type(long.class.getName());
    public static final Type OBJECT = new Type(Object.class.getName());
    public static final Type STRING = new Type(String.class.getName());
    public static final Type THROWABLE = new Type(Throwable.class.getName());
    public static final Type MAP = new Type(Map.class.getName());
    public static final Type LIST = new Type(List.class.getName());
    public static final Type SET = new Type(Set.class.getName());
    public static final Type COLLECTION = new Type(Collection.class.getName());
    public static final Type BIG_DECIMAL = new Type(BigDecimal.class.getName());
    public static final Type BIG_INTEGER = new Type(BigInteger.class.getName());
    public static final Type URI = new Type(java.net.URI.class.getName());
    public static final Type DATE = new Type(Date.class.getName());
    public static final Type TEMPORAL_ACCESSOR = new Type(TemporalAccessor.class.getName());
    public static final Type STREAM = new Type(Stream.class.getName());
    public static final Type SUPPLIER = new Type(Supplier.class.getName());
    public static final Type BI_CONSUMER = new Type(BiConsumer.class.getName());
    public static final Type CONSUMER = new Type(Consumer.class.getName());

    public static final Type RESPONSE = new Type(Response.class.getName());
    public static final Type RESPONSE_BUILDER = new Type(Response.ResponseBuilder.class.getName());
    public static final Type RESPONSE_STATUS = new Type(Response.Status.class.getName());
    public static final Type RESOURCE_CONTEXT = new Type(ResourceContext.class.getName());
    public static final Type GENERIC_ENTITY = new Type(GenericEntity.class.getName());
    public static final Type VARIANT = new Type(Variant.class.getName());
    public static final Type ENTITY_TAG = new Type(EntityTag.class.getName());
    public static final Type WEB_APPLICATION_EXCEPTION = new Type(WebApplicationException.class.getName());

    public static final Type JSON = new Type(Json.class.getName());
    public static final Type JSON_OBJECT_BUILDER = new Type(JsonObjectBuilder.class.getName());
    public static final Type JSON_ARRAY_BUILDER = new Type(JsonArrayBuilder.class.getName());
    public static final Type JSON_VALUE = new Type(JsonValue.class.getName());
    public static final Type JSON_OBJECT = new Type(JsonObject.class.getName());
    public static final Type JSON_ARRAY = new Type(JsonArray.class.getName());

    public static final Set<Type> INTEGER_TYPES = new HashSet<>(Arrays.asList(INTEGER, PRIMITIVE_INT, BIG_INTEGER, LONG, PRIMITIVE_LONG, SHORT, PRIMITIVE_SHORT,
            BYTE, PRIMITIVE_BYTE));
    public static final Set<Type> DOUBLE_TYPES = new HashSet<>(Arrays.asList(DOUBLE, PRIMITIVE_DOUBLE, FLOAT, PRIMITIVE_FLOAT, BIG_DECIMAL));
    public static final Set<Type> JSON_TYPES = new HashSet<>(Arrays.asList(JSON_VALUE, JSON_OBJECT, JSON_ARRAY));

}
