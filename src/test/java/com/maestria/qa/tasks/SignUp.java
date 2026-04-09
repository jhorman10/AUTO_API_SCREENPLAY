package com.maestria.qa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.maestria.qa.utils.RestContext;
import com.maestria.qa.utils.TestConstants;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class SignUp implements Task {

    private static final Logger logger = LoggerFactory.getLogger(SignUp.class);

    private final String email;
    private final String password;

    public SignUp(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Authenticating via Firebase signIn: {}", email);

        JsonObject body = new JsonObject();
        body.addProperty(TestConstants.Payload.EMAIL, email);
        body.addProperty(TestConstants.Payload.PASSWORD, password);
        body.addProperty(TestConstants.Payload.RETURN_SECURE_TOKEN, true);

        Response signInResponse = RestAssured.given()
                .baseUri(TestConstants.Api.FIREBASE_BASE_URI)
                .urlEncodingEnabled(false)
                .contentType(TestConstants.Api.CONTENT_TYPE_JSON)
                .body(body.toString())
                .post(TestConstants.Api.FIREBASE_SIGNIN_PATH);

        if (signInResponse.getStatusCode() == 200) {
            String idToken = signInResponse.jsonPath().getString(TestConstants.Payload.ID_TOKEN);
            RestContext.setAuthToken(idToken);
            logger.info("Firebase signIn successful, token length: {}", idToken.length());
        } else {
            logger.error("Firebase signIn failed with status: {}", signInResponse.getStatusCode());
            logger.error("Response: {}", signInResponse.asString());
        }
    }

    public static SignUp withCredentials(String email, String password) {
        return new SignUp(email, password);
    }

    public static SignUp withTestCredentials() {
        return new SignUp(TestConstants.Auth.TEST_EMAIL, TestConstants.Auth.TEST_PASSWORD);
    }
}
