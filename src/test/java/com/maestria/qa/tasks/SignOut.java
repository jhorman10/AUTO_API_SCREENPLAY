package com.maestria.qa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.utils.RestContext;

import io.restassured.RestAssured;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class SignOut implements Task {

    private static final Logger logger = LoggerFactory.getLogger(SignOut.class);

    private final String token;

    public SignOut(String token) {
        this.token = token;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Signing out user");

        RestContext.setLastResponse(
            RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .post("/auth/signOut")
        );

        logger.info("POST /auth/signOut executed");
    }

    public static SignOut withToken(String token) {
        return new SignOut(token);
    }
}
