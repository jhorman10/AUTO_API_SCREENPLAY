package com.maestria.qa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.utils.RestContext;
import com.maestria.qa.utils.TestConstants;

import io.restassured.RestAssured;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class GetTurnoById implements Task {

    private static final Logger logger = LoggerFactory.getLogger(GetTurnoById.class);

    private final String idCard;

    public GetTurnoById(String idCard) {
        this.idCard = idCard;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Fetching appointment by idCard: {}", idCard);

        RestContext.setLastResponse(
                RestAssured.given()
                        .get(TestConstants.Api.APPOINTMENTS_ENDPOINT
                                + TestConstants.Api.PATH_SEPARATOR + idCard)
        );

        logger.info("GET {}/{} executed", TestConstants.Api.APPOINTMENTS_ENDPOINT, idCard);
    }

    public static GetTurnoById withId(String idCard) {
        return new GetTurnoById(idCard);
    }
}
