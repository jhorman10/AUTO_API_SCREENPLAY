package com.maestria.qa.utils;

import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.HttpClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiConfig {
    private static final Logger logger = LoggerFactory.getLogger(ApiConfig.class);
    
    private static String baseUrl;
    private static int timeout;
    private static int maxRetries;
    
    static {
        loadConfiguration();
    }
    
    private static void loadConfiguration() {
        baseUrl = getProperty("api.base.url", "http://localhost:8000/api/v1");
        timeout = Integer.parseInt(getProperty("api.timeout", "5000"));
        maxRetries = Integer.parseInt(getProperty("api.max.retries", "1"));
        
        logger.info("API Configuration loaded:");
        logger.info("  Base URL: {}", baseUrl);
        logger.info("  Timeout: {}ms", timeout);
        logger.info("  Max Retries: {}", maxRetries);
        
        configureRestAssured();
    }
    
    private static void configureRestAssured() {
        RestAssured.baseURI = baseUrl;
        RestAssured.config = RestAssuredConfig.config()
            .httpClient(HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", timeout)
                .setParam("http.socket.timeout", timeout)
            );
    }
    
    private static String getProperty(String key, String defaultValue) {
        return System.getProperty(key, defaultValue);
    }
    
    public static String getBaseUrl() {
        return baseUrl;
    }
    
    public static int getTimeout() {
        return timeout;
    }
    
    public static int getMaxRetries() {
        return maxRetries;
    }
    
    public static void setBaseUrl(String url) {
        baseUrl = url;
        RestAssured.baseURI = url;
    }
}
