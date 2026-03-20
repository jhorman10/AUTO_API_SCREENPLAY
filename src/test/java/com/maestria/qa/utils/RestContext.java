package com.maestria.qa.utils;

import io.restassured.response.Response;

public class RestContext {

    private static final ThreadLocal<Response> lastResponse = new ThreadLocal<>();

    public static void setLastResponse(Response response) {
        lastResponse.set(response);
    }

    public static Response getLastResponse() {
        return lastResponse.get();
    }

    public static void clear() {
        lastResponse.remove();
    }
}
