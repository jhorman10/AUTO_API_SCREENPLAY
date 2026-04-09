package com.maestria.qa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.maestria.qa.utils.RestContext;
import com.maestria.qa.utils.TestConstants;

import io.restassured.RestAssured;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class CreateTurnoWithUrgency implements Task {

    private static final Logger logger = LoggerFactory.getLogger(CreateTurnoWithUrgency.class);

    private final long idCard;
    private final String fullName;
    private final String priority;

    public CreateTurnoWithUrgency(long idCard, String fullName, String priority) {
        this.idCard = idCard;
        this.fullName = fullName;
        this.priority = priority;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Creating appointment for patient: {} - {} (priority: {})", idCard, fullName, priority);

        JsonObject body = new JsonObject();
        body.addProperty(TestConstants.Payload.ID_CARD, idCard);
        body.addProperty(TestConstants.Payload.FULL_NAME, fullName);
        body.addProperty(TestConstants.Payload.PRIORITY, priority);

        String token = RestContext.getAuthToken();
        RestContext.setLastResponse(
                RestAssured.given()
                        .contentType(TestConstants.Api.CONTENT_TYPE_JSON)
                        .header("Authorization", "Bearer " + token)
                        .body(body.toString())
                        .post(TestConstants.Api.APPOINTMENTS_ENDPOINT)
        );

        logger.info("POST {} executed with priority: {}", TestConstants.Api.APPOINTMENTS_ENDPOINT, priority);
    }

    public static CreateTurnoWithUrgency forPatient(long idCard, String fullName, String priority) {
        return new CreateTurnoWithUrgency(idCard, fullName, priority);
    }
}
