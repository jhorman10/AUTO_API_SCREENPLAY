package com.maestria.qa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.utils.RestContext;

import io.restassured.RestAssured;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class GetMe implements Task {

    private static final Logger logger = LoggerFactory.getLogger(GetMe.class);

    private final String token;

    public GetMe(String token) {
        this.token = token;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Fetching current user profile with token");

        RestContext.setLastResponse(
            RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .get("/auth/me")
        );

        logger.info("GET /auth/me executed");
    }

    public static GetMe withToken(String token) {
        return new GetMe(token);
    }
}
