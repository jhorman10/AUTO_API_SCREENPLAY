package com.maestria.qa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.utils.RestContext;
import com.maestria.qa.utils.TestConstants;

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
                .get(TestConstants.Api.TURNOS_ENDPOINT)
        );

        logger.info("GET {} executed", TestConstants.Api.TURNOS_ENDPOINT);
    }

    public static GetAllTurnos fromTheApi() {
        return new GetAllTurnos();
    }
}
