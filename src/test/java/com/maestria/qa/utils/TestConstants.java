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

        public static final String APPOINTMENTS_ENDPOINT = "/appointments";
        public static final String QUEUE_POSITION_ENDPOINT = "/appointments/queue-position";
        public static final String DOCTORS_ENDPOINT = "/doctors";
        public static final String AUDIT_LOGS_ENDPOINT = "/audit-logs";
        public static final String PATH_SEPARATOR = "/";
        public static final String TURNOS_ENDPOINT = "/turnos";

        public static final String FIREBASE_API_KEY = "AIzaSyB2i40m9MQpOWxERwqaHxf3MHTaNrSZj7U";
        public static final String FIREBASE_BASE_URI = "https://identitytoolkit.googleapis.com";
        public static final String FIREBASE_SIGNIN_PATH
                = "/v1/accounts:signInWithPassword?key=" + FIREBASE_API_KEY;

        private Api() {
        }
    }

    public static final class Payload {

        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
        public static final String FULL_NAME = "fullName";
        public static final String ID_CARD = "idCard";
        public static final String PRIORITY = "priority";
        public static final String STATUS = "status";
        public static final String DOCTOR_NAME = "doctorName";
        public static final String DOCTOR_ID = "doctorId";
        public static final String OFFICE = "office";
        public static final String TIMESTAMP = "timestamp";
        public static final String ID = "id";
        public static final String MESSAGE = "message";
        public static final String POSITION = "position";
        public static final String TOTAL = "total";
        public static final String ROOT_LIST = "$";
        public static final String ID_TOKEN = "idToken";
        public static final String RETURN_SECURE_TOKEN = "returnSecureToken";
        public static final String NOMBRE = "nombre";
        public static final String ROL = "rol";
        public static final String CEDULA = "cedula";
        public static final String TOKEN = "token";
        public static final String ROLE = "empleado";
        public static final long INVALID_CEDULA = 0L;

        private Payload() {
        }
    }

    public static final class Priority {

        public static final String HIGH = "high";
        public static final String MEDIUM = "medium";
        public static final String LOW = "low";

        private Priority() {
        }
    }

    public static final class State {

        public static final String WAITING = "waiting";
        public static final String ASSIGNED = "assigned";
        public static final String CALLED = "called";
        public static final String COMPLETED = "completed";
        public static final String CANCELLED = "cancelled";
        public static final String NOT_FOUND = "not_found";

        private State() {
        }
    }

    public static final class Polling {

        public static final int INTERVAL_MS = 2000;
        public static final int MAX_RETRIES = 5;
        public static final int TIMEOUT_MS = 10000;

        private Polling() {
        }
    }

    public static final class Auth {

        public static final String TEST_EMAIL = "qa_hu03_auto@test.com";
        public static final String TEST_PASSWORD = "QaTest2024!";

        private Auth() {
        }
    }

    public static final class Email {

        public static final String ADDRESS_SEPARATOR = "@";
        public static final String ALIAS_SEPARATOR = "+";
        public static final int ADDRESS_PARTS = 2;

        private Email() {
        }
    }

    public static final class Defaults {

        public static final String USER_NAME = "QA Tester";
        public static final String ROLE = "recepcionista";
        public static final String PRIORITY = "medium";
        public static final String ACTOR_NAME = "ApiTester";
        public static final String SERENITY_PROJECT_NAME = "AUTO_API_SCREENPLAY";
        public static final long INVALID_ID_CARD = 0L;
        public static final long INVALID_CEDULA = INVALID_ID_CARD;

        private Defaults() {
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
