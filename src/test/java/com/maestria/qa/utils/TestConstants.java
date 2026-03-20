package com.maestria.qa.utils;

public final class TestConstants {

    private TestConstants() {
    }

    public static final class Api {
        public static final String BASE_URL_PROPERTY = "api.base.url";
        public static final String TIMEOUT_PROPERTY = "api.timeout";
        public static final String MAX_RETRIES_PROPERTY = "api.max.retries";
        public static final String DEFAULT_BASE_URL = "http://localhost:3000";
        public static final int DEFAULT_TIMEOUT_MS = 5000;
        public static final int DEFAULT_MAX_RETRIES = 1;
        public static final String CONTENT_TYPE_JSON = "application/json";
        public static final String CONNECTION_TIMEOUT_PARAM = "http.connection.timeout";
        public static final String SOCKET_TIMEOUT_PARAM = "http.socket.timeout";
        public static final String SIGN_UP_ENDPOINT = "/auth/signUp";
        public static final String TURNOS_ENDPOINT = "/turnos";
        public static final String PATH_SEPARATOR = "/";

        private Api() {
        }
    }

    public static final class Payload {
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
        public static final String NOMBRE = "nombre";
        public static final String ROL = "rol";
        public static final String CEDULA = "cedula";
        public static final String PRIORITY = "priority";
        public static final String TOKEN = "token";
        public static final String MESSAGE = "message";
        public static final String ROOT_LIST = "$";

        private Payload() {
        }
    }

    public static final class Defaults {
        public static final String USER_NAME = "QA Tester";
        public static final String ROLE = "empleado";
        public static final String PRIORITY = "media";
        public static final String ACTOR_NAME = "ApiTester";
        public static final String SERENITY_PROJECT_NAME = "AUTO_API_SCREENPLAY";
        public static final long INVALID_CEDULA = 0L;

        private Defaults() {
        }
    }

    public static final class Email {
        public static final String ADDRESS_SEPARATOR = "@";
        public static final String ALIAS_SEPARATOR = "+";
        public static final int ADDRESS_PARTS = 2;

        private Email() {
        }
    }

    public static final class Cucumber {
        public static final String FEATURES_PATH = "src/test/resources/features";
        public static final String GLUE_PATH = "com.maestria.qa.stepdefs";
        public static final String PRETTY_PLUGIN = "pretty";
        public static final String JUNIT_PLUGIN = "junit:target/cucumber-reports/cucumber.xml";
        public static final String JSON_PLUGIN = "json:target/cucumber-reports/cucumber.json";
        public static final String HTML_PLUGIN = "html:target/cucumber-reports/cucumber.html";
        public static final String DEFAULT_TAG_FILTER = "not @wip";

        private Cucumber() {
        }
    }

    public static final class Reports {
        public static final String CUCUMBER_REPORTS_DIRECTORY = "target/cucumber-reports";
        public static final String CUCUMBER_XML_REPORT = "target/cucumber-reports/cucumber.xml";
        public static final String CUCUMBER_JSON_REPORT = "target/cucumber-reports/cucumber.json";
        public static final String CUCUMBER_HTML_REPORT = "target/cucumber-reports/cucumber.html";
        public static final String SERENITY_RESULTS_DIRECTORY = "target/site/serenity";
        public static final String SERENITY_INDEX_REPORT = "target/site/serenity/index.html";

        private Reports() {
        }
    }
}