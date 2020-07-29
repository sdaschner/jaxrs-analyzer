package com.sebastian_daschner.jaxrs_analyzer.backend;

import javax.ws.rs.core.Response.Status;

public final class BackendUtils {

	public static String getReason(int statusCode) {
		Status status = Status.fromStatusCode(statusCode);
		return status == null ? String.valueOf(statusCode) : status.getReasonPhrase();
	}

	private BackendUtils() {}
}
