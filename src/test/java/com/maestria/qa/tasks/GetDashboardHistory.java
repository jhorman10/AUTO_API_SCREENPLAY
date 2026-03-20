package com.maestria.qa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.utils.RestContext;

import io.restassured.RestAssured;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class GetDashboardHistory implements Task {

    private static final Logger logger = LoggerFactory.getLogger(GetDashboardHistory.class);

    private final String token;

    public GetDashboardHistory(String token) {
        this.token = token;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Fetching dashboard history with token");

        RestContext.setLastResponse(
            RestAssured.given()
                .header("Authorization", "Bearer " + token)
                .get("/auth/dashboard-history")
        );

        logger.info("GET /auth/dashboard-history executed");
    }

    public static GetDashboardHistory withToken(String token) {
        return new GetDashboardHistory(token);
    }
}
