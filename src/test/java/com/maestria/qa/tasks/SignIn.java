package com.maestria.qa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.maestria.qa.utils.RestContext;

import io.restassured.RestAssured;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class SignIn implements Task {

    private static final Logger logger = LoggerFactory.getLogger(SignIn.class);

    private final String email;
    private final String password;

    public SignIn(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Signing in user with email: {}", email);

        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("password", password);

        RestContext.setLastResponse(
            RestAssured.given()
                .contentType("application/json")
                .body(body.toString())
                .post("/auth/signIn")
        );

        logger.info("POST /auth/signIn executed");
    }

    public static SignIn withCredentials(String email, String password) {
        return new SignIn(email, password);
    }
}
