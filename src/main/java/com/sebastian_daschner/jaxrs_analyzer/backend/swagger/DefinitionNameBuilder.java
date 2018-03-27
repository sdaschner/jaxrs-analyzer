package com.sebastian_daschner.jaxrs_analyzer.backend.swagger;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.utils.Pair;

import javax.json.JsonObject;
import java.util.Map;

class DefinitionNameBuilder {

    String buildDefinitionName(final String typeName, Map<String, Pair<String, JsonObject>> jsonDefinitions) {
        String definition = buildDefinitionName(typeName);

        if (isOnlyType(typeName, definition, jsonDefinitions))
            return definition;

        // TODO can be problematic, see issue #155
        if (!definition.matches("_\\d+$"))
            return definition + "_2";

        final int separatorIndex = definition.lastIndexOf('_');
        final int index = Integer.parseInt(definition.substring(separatorIndex + 1));
        return definition.substring(0, separatorIndex + 1) + (index + 1);
    }

    private String buildDefinitionName(String typeName) {
        if (isDynamicType(typeName))
            return buildDynamicName(typeName);
        return buildJavaTypeName(typeName);
    }

    private boolean isDynamicType(String typeName) {
        return typeName.startsWith(TypeIdentifier.DYNAMIC_TYPE_PREFIX);
    }

    private String buildDynamicName(String typeName) {
        return "JsonObject" + extractDynamicSuffix(typeName);
    }

    private String extractDynamicSuffix(String typeName) {
        String id = typeName.substring(TypeIdentifier.DYNAMIC_TYPE_PREFIX.length());
        return "1".equals(id) ? "" : "_" + id;
    }

    private String buildJavaTypeName(String typeName) {
        return typeName.substring(typeName.lastIndexOf('/') + 1, typeName.length() - 1);
    }

    private boolean isOnlyType(String typeName, String definition, Map<String, Pair<String, JsonObject>> jsonDefinitions) {
        final Pair<String, JsonObject> containedEntry = jsonDefinitions.get(definition);
        return containedEntry == null || containedEntry.getLeft() != null && containedEntry.getLeft().equals(typeName);
    }

}