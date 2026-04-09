package com.maestria.qa.utils;

import io.restassured.response.Response;

public class RestContext {

    private static final ThreadLocal<Response> lastResponse = new ThreadLocal<>();
    private static final ThreadLocal<String> authToken = new ThreadLocal<>();

    public static void setLastResponse(Response response) {
        lastResponse.set(response);
    }

    public static Response getLastResponse() {
        return lastResponse.get();
    }

    public static void setAuthToken(String token) {
        authToken.set(token);
    }

    public static String getAuthToken() {
        return authToken.get();
    }

    public static void clear() {
        lastResponse.remove();
        authToken.remove();
    }
}
