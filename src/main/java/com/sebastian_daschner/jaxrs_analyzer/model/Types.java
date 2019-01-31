package com.sebastian_daschner.jaxrs_analyzer.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Sebastian Daschner
 */
public final class Types {

    private Types() {
        throw new UnsupportedOperationException();
    }

    public static final String OBJECT = "Ljava/lang/Object;";
    public static final String CLASS = "Ljava/lang/Class;";

    public static final String PATH = "Ljavax/ws/rs/Path;";
    public static final String APPLICATION_PATH = "Ljavax/ws/rs/ApplicationPath;";
    public static final String CONTEXT = "Ljavax/ws/rs/core/Context;";

    public static final String CONSUMES = "Ljavax/ws/rs/Consumes;";
    public static final String PRODUCES = "Ljavax/ws/rs/Produces;";

    public static final String GET = "Ljavax/ws/rs/GET;";
    public static final String POST = "Ljavax/ws/rs/POST;";
    public static final String PUT = "Ljavax/ws/rs/PUT;";
    public static final String DELETE = "Ljavax/ws/rs/DELETE;";
    public static final String HEAD = "Ljavax/ws/rs/HEAD;";
    public static final String OPTIONS = "Ljavax/ws/rs/OPTIONS;";
    public static final String SUSPENDED = "Ljavax/ws/rs/container/Suspended;";

    public static final String DEFAULT_VALUE = "Ljavax/ws/rs/DefaultValue;";
    public static final String PATH_PARAM = "Ljavax/ws/rs/PathParam;";
    public static final String QUERY_PARAM = "Ljavax/ws/rs/QueryParam;";
    public static final String HEADER_PARAM = "Ljavax/ws/rs/HeaderParam;";
    public static final String FORM_PARAM = "Ljavax/ws/rs/FormParam;";
    public static final String COOKIE_PARAM = "Ljavax/ws/rs/CookieParam;";
    public static final String MATRIX_PARAM = "Ljavax/ws/rs/MatrixParam;";
    public static final String BEAN_PARAM = "Ljavax/ws/rs/BeanParam;";

    public static final String DEPRECATED = "Ljava/lang/Deprecated;";

    public static final String PRIMITIVE_VOID = "V";
    public static final String BOOLEAN = "Ljava/lang/Boolean;";
    public static final String PRIMITIVE_BOOLEAN = "Z";
    public static final String CHARACTER = "Ljava/lang/Character;";
    public static final String PRIMITIVE_CHAR = "C";
    public static final String INTEGER = "Ljava/lang/Integer;";
    public static final String PRIMITIVE_INT = "I";
    public static final String BYTE = "Ljava/lang/Byte;";
    public static final String PRIMITIVE_BYTE = "B";
    public static final String SHORT = "Ljava/lang/Short;";
    public static final String PRIMITIVE_SHORT = "S";
    public static final String DOUBLE = "Ljava/lang/Double;";
    public static final String PRIMITIVE_DOUBLE = "D";
    public static final String FLOAT = "Ljava/lang/Float;";
    public static final String PRIMITIVE_FLOAT = "F";
    public static final String LONG = "Ljava/lang/Long;";
    public static final String PRIMITIVE_LONG = "J";
    public static final String STRING = "Ljava/lang/String;";
    public static final String THROWABLE = "Ljava/lang/Throwable;";
    public static final String MAP = "Ljava/util/Map;";
    public static final String LIST = "Ljava/util/List;";
    public static final String SET = "Ljava/util/Set;";
    public static final String COLLECTION = "Ljava/util/Collection;";
    public static final String BIG_DECIMAL = "Ljava/math/BigDecimal;";
    public static final String BIG_INTEGER = "Ljava/math/BigInteger;";
    public static final String URI = "Ljava/net/URI;";
    public static final String DATE = "Ljava/util/Date;";
    public static final String TEMPORAL_ACCESSOR = "Ljava/time/temporal/TemporalAccessor;";
    public static final String STREAM = "Ljava/util/stream/Stream;";
    public static final String SUPPLIER = "Ljava/util/function/Supplier;";
    public static final String BI_CONSUMER = "Ljava/util/function/BiConsumer;";
    public static final String CONSUMER = "Ljava/util/function/Consumer;";

    public static final String RESPONSE = "Ljavax/ws/rs/core/Response;";
    public static final String RESPONSE_BUILDER = "Ljavax/ws/rs/core/Response$ResponseBuilder;";
    public static final String RESPONSE_STATUS = "Ljavax/ws/rs/core/Response$Status;";
    public static final String GENERIC_ENTITY = "Ljavax/ws/rs/core/GenericEntity;";
    public static final String VARIANT = "Ljavax/ws/rs/core/Variant;";
    public static final String ENTITY_TAG = "Ljavax/ws/rs/core/EntityTag;";

    public static final String JSON = "Ljavax/json/Json;";
    public static final String JSON_OBJECT_BUILDER = "Ljavax/json/JsonObjectBuilder;";
    public static final String JSON_ARRAY_BUILDER = "Ljavax/json/JsonArrayBuilder;";
    public static final String JSON_VALUE = "Ljavax/json/JsonValue;";
    public static final String JSON_OBJECT = "Ljavax/json/JsonObject;";
    public static final String JSON_ARRAY = "Ljavax/json/JsonArray;";

    public static final String CLASS_OBJECT = "java/lang/Object";

    public static final String CLASS_PRIMITIVE_VOID = "void";
    public static final String CLASS_PRIMITIVE_BOOLEAN = "boolean";
    public static final String CLASS_PRIMITIVE_CHAR = "char";
    public static final String CLASS_PRIMITIVE_INT = "int";
    public static final String CLASS_PRIMITIVE_BYTE = "byte";
    public static final String CLASS_PRIMITIVE_SHORT = "short";
    public static final String CLASS_PRIMITIVE_DOUBLE = "double";
    public static final String CLASS_PRIMITIVE_FLOAT = "float";
    public static final String CLASS_PRIMITIVE_LONG = "long";

    public static final String CLASS_RESPONSE = "javax/ws/rs/core/Response";
    public static final String CLASS_RESPONSE_BUILDER = "javax/ws/rs/core/Response$ResponseBuilder";
    public static final String CLASS_RESOURCE_CONTEXT = "javax/ws/rs/container/ResourceContext";
    public static final String CLASS_WEB_APPLICATION_EXCEPTION = "javax/ws/rs/WebApplicationException";

    public static final String CLASS_LIST = "java/util/List";
    public static final String CLASS_SET = "java/util/Set";
    public static final String CLASS_INTEGER = "java/lang/Integer";
    public static final String CLASS_DOUBLE = "java/lang/Double";
    public static final String CLASS_LONG = "java/lang/Long";
    public static final String CLASS_STREAM = "java/util/stream/Stream";

    public static final String CLASS_JSON = "javax/json/Json";
    public static final String CLASS_JSON_OBJECT_BUILDER = "javax/json/JsonObjectBuilder";
    public static final String CLASS_JSON_ARRAY_BUILDER = "javax/json/JsonArrayBuilder";
    public static final String CLASS_JSON_OBJECT = "javax/json/JsonObject";

    public static final Set<String> PRIMITIVE_TYPES = new HashSet<>(Arrays.asList(PRIMITIVE_BOOLEAN, PRIMITIVE_VOID, PRIMITIVE_CHAR, PRIMITIVE_INT, PRIMITIVE_LONG,
            PRIMITIVE_SHORT, PRIMITIVE_BYTE, PRIMITIVE_DOUBLE, PRIMITIVE_FLOAT));
    public static final Set<String> PRIMITIVE_TYPES_ALL = new HashSet<>(Arrays.asList(PRIMITIVE_BOOLEAN, BOOLEAN, PRIMITIVE_VOID, PRIMITIVE_CHAR, CHARACTER,
            PRIMITIVE_INT, INTEGER, PRIMITIVE_LONG, LONG, PRIMITIVE_SHORT, SHORT, PRIMITIVE_BYTE, BYTE, PRIMITIVE_DOUBLE, DOUBLE, PRIMITIVE_FLOAT, FLOAT));
    public static final Set<String> INTEGER_TYPES = new HashSet<>(Arrays.asList(INTEGER, PRIMITIVE_INT, BIG_INTEGER, LONG, PRIMITIVE_LONG, SHORT, PRIMITIVE_SHORT,
            BYTE, PRIMITIVE_BYTE));
    public static final Set<String> DOUBLE_TYPES = new HashSet<>(Arrays.asList(DOUBLE, PRIMITIVE_DOUBLE, FLOAT, PRIMITIVE_FLOAT, BIG_DECIMAL));
    public static final Set<String> JSON_TYPES = new HashSet<>(Arrays.asList(JSON_VALUE, JSON_OBJECT, JSON_ARRAY));

}
