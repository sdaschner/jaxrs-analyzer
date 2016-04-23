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

import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonArray;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonObject;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.IdentifiableMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.MethodIdentifier;

import java.util.List;
import java.util.function.BiFunction;

import static com.sebastian_daschner.jaxrs_analyzer.model.Types.*;

/**
 * Known JSON methods which apply logic to the result or to the return element.
 *
 * @author Sebastian Daschner
 */
enum KnownJsonResultMethod implements IdentifiableMethod {

    JSON_ARRAY_BUILDER_CREATE(MethodIdentifier.ofStatic(CLASS_JSON, "createArrayBuilder", JSON_ARRAY_BUILDER), (object, arguments) -> new Element(JSON_ARRAY, new JsonArray())),

    JSON_ARRAY_BUILDER_ADD_BIG_DECIMAL(MethodIdentifier.ofNonStatic(CLASS_JSON_ARRAY_BUILDER, "add", JSON_ARRAY_BUILDER, BIG_DECIMAL),
            (object, arguments) -> addToArray(object, arguments, BIG_DECIMAL)),

    JSON_ARRAY_BUILDER_ADD_BIG_INTEGER(MethodIdentifier.ofNonStatic(CLASS_JSON_ARRAY_BUILDER, "add", JSON_ARRAY_BUILDER, BIG_INTEGER),
            (object, arguments) -> addToArray(object, arguments, BIG_INTEGER)),

    JSON_ARRAY_BUILDER_ADD_STRING(MethodIdentifier.ofNonStatic(CLASS_JSON_ARRAY_BUILDER, "add", JSON_ARRAY_BUILDER, STRING),
            (object, arguments) -> addToArray(object, arguments, STRING)),

    JSON_ARRAY_BUILDER_ADD_INT(MethodIdentifier.ofNonStatic(CLASS_JSON_ARRAY_BUILDER, "add", JSON_ARRAY_BUILDER, PRIMITIVE_INT),
            (object, arguments) -> addToArray(object, arguments, INTEGER)),

    JSON_ARRAY_BUILDER_ADD_LONG(MethodIdentifier.ofNonStatic(CLASS_JSON_ARRAY_BUILDER, "add", JSON_ARRAY_BUILDER, PRIMITIVE_LONG),
            (object, arguments) -> addToArray(object, arguments, LONG)),

    JSON_ARRAY_BUILDER_ADD_DOUBLE(MethodIdentifier.ofNonStatic(CLASS_JSON_ARRAY_BUILDER, "add", JSON_ARRAY_BUILDER, PRIMITIVE_DOUBLE),
            (object, arguments) -> addToArray(object, arguments, DOUBLE)),

    JSON_ARRAY_BUILDER_ADD_BOOLEAN(MethodIdentifier.ofNonStatic(CLASS_JSON_ARRAY_BUILDER, "add", JSON_ARRAY_BUILDER, PRIMITIVE_BOOLEAN),
            (object, arguments) -> addToArray(object, arguments, PRIMITIVE_BOOLEAN)),

    JSON_ARRAY_BUILDER_ADD_JSON(MethodIdentifier.ofNonStatic(CLASS_JSON_ARRAY_BUILDER, "add", JSON_ARRAY_BUILDER, JSON_VALUE),
            KnownJsonResultMethod::addToArray),

    JSON_ARRAY_BUILDER_ADD_JSON_OBJECT(MethodIdentifier.ofNonStatic(CLASS_JSON_ARRAY_BUILDER, "add", JSON_ARRAY_BUILDER, JSON_OBJECT_BUILDER), (object, arguments) ->
            addToArray(object, arguments, JSON_OBJECT)),

    JSON_ARRAY_BUILDER_ADD_JSON_ARRAY(MethodIdentifier.ofNonStatic(CLASS_JSON_ARRAY_BUILDER, "add", JSON_ARRAY_BUILDER, JSON_ARRAY_BUILDER), (object, arguments) ->
            addToArray(object, arguments, JSON_ARRAY)),

    JSON_ARRAY_BUILDER_ADD_NULL(MethodIdentifier.ofNonStatic(CLASS_JSON_ARRAY_BUILDER, "addNull", JSON_ARRAY_BUILDER),
            (object, arguments) -> {
                object.getPossibleValues().stream().filter(o -> o instanceof JsonArray).map(o -> (JsonArray) o)
                        .forEach(a -> a.getElements().add(new Element(OBJECT, null)));
                return object;
            }),

    JSON_ARRAY_BUILDER_BUILD(MethodIdentifier.ofNonStatic(CLASS_JSON_ARRAY_BUILDER, "build", JSON_ARRAY), (object, arguments) -> {
        Element json = new Element(JSON_ARRAY);
        json.getPossibleValues().addAll(object.getPossibleValues());
        return json;
    }),

    JSON_OBJECT_BUILDER_CREATE(MethodIdentifier.ofStatic(CLASS_JSON, "createObjectBuilder", JSON_OBJECT_BUILDER), (object, arguments) -> new Element(JSON_OBJECT, new JsonObject())),

    JSON_OBJECT_BUILDER_ADD_BIG_DECIMAL(MethodIdentifier.ofNonStatic(CLASS_JSON_OBJECT_BUILDER, "add", JSON_OBJECT_BUILDER, STRING, BIG_DECIMAL),
            (object, arguments) -> mergeJsonStructure(object, arguments, BIG_DECIMAL)),

    JSON_OBJECT_BUILDER_ADD_BIG_INTEGER(MethodIdentifier.ofNonStatic(CLASS_JSON_OBJECT_BUILDER, "add", JSON_OBJECT_BUILDER, STRING, BIG_INTEGER),
            (object, arguments) -> mergeJsonStructure(object, arguments, BIG_INTEGER)),

    JSON_OBJECT_BUILDER_ADD_STRING(MethodIdentifier.ofNonStatic(CLASS_JSON_OBJECT_BUILDER, "add", JSON_OBJECT_BUILDER, STRING, STRING),
            (object, arguments) -> mergeJsonStructure(object, arguments, STRING)),

    JSON_OBJECT_BUILDER_ADD_INT(MethodIdentifier.ofNonStatic(CLASS_JSON_OBJECT_BUILDER, "add", JSON_OBJECT_BUILDER, STRING, PRIMITIVE_INT),
            (object, arguments) -> mergeJsonStructure(object, arguments, INTEGER)),

    JSON_OBJECT_BUILDER_ADD_LONG(MethodIdentifier.ofNonStatic(CLASS_JSON_OBJECT_BUILDER, "add", JSON_OBJECT_BUILDER, STRING, PRIMITIVE_LONG),
            (object, arguments) -> mergeJsonStructure(object, arguments, LONG)),

    JSON_OBJECT_BUILDER_ADD_DOUBLE(MethodIdentifier.ofNonStatic(CLASS_JSON_OBJECT_BUILDER, "add", JSON_OBJECT_BUILDER, STRING, PRIMITIVE_DOUBLE),
            (object, arguments) -> mergeJsonStructure(object, arguments, DOUBLE)),

    JSON_OBJECT_BUILDER_ADD_BOOLEAN(MethodIdentifier.ofNonStatic(CLASS_JSON_OBJECT_BUILDER, "add", JSON_OBJECT_BUILDER, STRING, PRIMITIVE_BOOLEAN),
            (object, arguments) -> mergeJsonStructure(object, arguments, PRIMITIVE_BOOLEAN)),

    JSON_OBJECT_BUILDER_ADD_JSON(MethodIdentifier.ofNonStatic(CLASS_JSON_OBJECT_BUILDER, "add", JSON_OBJECT_BUILDER, STRING, JSON_VALUE),
            KnownJsonResultMethod::mergeJsonStructure),

    JSON_OBJECT_BUILDER_ADD_JSON_OBJECT(MethodIdentifier.ofNonStatic(CLASS_JSON_OBJECT_BUILDER, "add", JSON_OBJECT_BUILDER, STRING, JSON_OBJECT_BUILDER), (object, arguments) ->
            mergeJsonStructure(object, arguments, JSON_OBJECT)),

    JSON_OBJECT_BUILDER_ADD_JSON_ARRAY(MethodIdentifier.ofNonStatic(CLASS_JSON_OBJECT_BUILDER, "add", JSON_OBJECT_BUILDER, STRING, JSON_ARRAY_BUILDER), (object, arguments) ->
            mergeJsonStructure(object, arguments, JSON_ARRAY)),

    JSON_OBJECT_BUILDER_ADD_NULL(MethodIdentifier.ofNonStatic(CLASS_JSON_OBJECT_BUILDER, "addNull", JSON_OBJECT_BUILDER, STRING),
            (object, arguments) -> {
                object.getPossibleValues().stream()
                        .filter(o -> o instanceof JsonObject).map(o -> (JsonObject) o)
                        .forEach(o -> arguments.get(0).getPossibleValues().stream().map(s -> (String) s)
                                .forEach(s -> o.getStructure().merge(s, new Element(OBJECT, null), Element::merge)));
                return object;
            }),

    JSON_OBJECT_BUILDER_BUILD(MethodIdentifier.ofNonStatic(CLASS_JSON_OBJECT_BUILDER, "build", JSON_OBJECT), (object, arguments) -> {
        final Element json = new Element(JSON_OBJECT);
        json.getPossibleValues().addAll(object.getPossibleValues());
        return json;
    }),

    JSON_OBJECT_GET_BOOLEAN(MethodIdentifier.ofNonStatic(CLASS_JSON_OBJECT, "getBoolean", PRIMITIVE_BOOLEAN, STRING),
            (object, arguments) -> object.getPossibleValues().stream()
                    .filter(o -> o instanceof JsonObject).map(o -> (JsonObject) o)
                    .map(o -> arguments.get(0).getPossibleValues().stream()
                            .map(s -> (String) s).map(s -> o.getStructure().get(s))
                            .reduce(new Element(PRIMITIVE_BOOLEAN), Element::merge))
                    .reduce(new Element(PRIMITIVE_BOOLEAN), Element::merge));

    private final MethodIdentifier identifier;

    private final BiFunction<Element, List<Element>, Element> function;

    KnownJsonResultMethod(final MethodIdentifier identifier,
                          final BiFunction<Element, List<Element>, Element> function) {
        this.identifier = identifier;
        this.function = function;
    }

    @Override
    public Element invoke(final Element object, final List<Element> arguments) {
        if (arguments.size() != identifier.getParameters())
            throw new IllegalArgumentException("Method arguments do not match expected signature!");

        return function.apply(object, arguments);
    }

    @Override
    public boolean matches(final MethodIdentifier identifier) {
        return this.identifier.equals(identifier);
    }

    private static Element addToArray(final Element object, final List<Element> arguments) {
        return addToArray(object, arguments.get(0));
    }

    private static Element addToArray(final Element object, final List<Element> arguments, final String typeOverride) {
        final Element element = new Element(typeOverride);
        element.getPossibleValues().addAll(arguments.get(0).getPossibleValues());
        return addToArray(object, element);
    }

    private static Element addToArray(final Element object, final Element argument) {
        object.getPossibleValues().stream()
                .filter(o -> o instanceof JsonArray).map(o -> (JsonArray) o)
                .forEach(a -> a.getElements().add(argument));
        return object;
    }

    private static Element mergeJsonStructure(final Element object, final List<Element> arguments) {
        final Element element = new Element(arguments.get(1).getTypes());
        element.merge(arguments.get(1));
        return mergeJsonStructure(object, arguments.get(0), element);
    }

    private static Element mergeJsonStructure(final Element object, final List<Element> arguments, final String typeOverride) {
        final Element element = new Element(typeOverride);
        element.getPossibleValues().addAll(arguments.get(1).getPossibleValues());
        return mergeJsonStructure(object, arguments.get(0), element);
    }

    private static Element mergeJsonStructure(final Element object, final Element key, final Element argument) {
        object.getPossibleValues().stream()
                .filter(o -> o instanceof JsonObject).map(o -> (JsonObject) o)
                .forEach(o -> key.getPossibleValues().stream().map(s -> (String) s)
                        .forEach(s -> o.getStructure().merge(s, argument, Element::merge)));
        return object;
    }

}

