package com.sebastian_daschner.jaxrs_analyzer.analysis.javadoc;

import com.sebastian_daschner.jaxrs_analyzer.LogProvider;
import com.sebastian_daschner.jaxrs_analyzer.utils.Pair;

class ResponseCommentExtractor {

    private ResponseCommentExtractor() {
    }

    static final String RESPONSE_TAG_NAME = "response";

    static Pair<Integer, String> extract(String comment) {
        try {
            String commentText = comment.trim();
            String statusPart = commentText.split("\\s")[0];
            int status = Integer.parseInt(statusPart);
            return Pair.of(status, commentText.substring(statusPart.length()).trim());
        } catch (Exception e) {
            LogProvider.info("Warning: malformed @response JavaDoc tag: '@response " + comment + "'");
            LogProvider.debug(e);
            return null;
        }
    }

}
