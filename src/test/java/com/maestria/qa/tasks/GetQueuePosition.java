package com.maestria.qa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.utils.RestContext;
import com.maestria.qa.utils.TestConstants;

import io.restassured.RestAssured;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class GetQueuePosition implements Task {

    private static final Logger logger = LoggerFactory.getLogger(GetQueuePosition.class);

    private final String idCard;

    public GetQueuePosition(String idCard) {
        this.idCard = idCard;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Fetching queue position for idCard: {}", idCard);

        RestContext.setLastResponse(
                RestAssured.given()
                        .get(TestConstants.Api.QUEUE_POSITION_ENDPOINT
                                + TestConstants.Api.PATH_SEPARATOR + idCard)
        );

        logger.info("GET {}/{} executed", TestConstants.Api.QUEUE_POSITION_ENDPOINT, idCard);
    }

    public static GetQueuePosition forIdCard(String idCard) {
        return new GetQueuePosition(idCard);
    }
}
