package com.maestria.qa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.utils.RestContext;

import io.restassured.RestAssured;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class GetAllTurnos implements Task {

    private static final Logger logger = LoggerFactory.getLogger(GetAllTurnos.class);

    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Fetching all turnos from API");

        RestContext.setLastResponse(
            RestAssured.given()
                .get("/turnos")
        );

        logger.info("GET /turnos executed");
    }

    public static GetAllTurnos fromTheApi() {
        return new GetAllTurnos();
    }
}
