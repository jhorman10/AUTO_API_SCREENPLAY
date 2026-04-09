package com.maestria.qa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.maestria.qa.utils.RestContext;
import com.maestria.qa.utils.TestConstants;

import io.restassured.RestAssured;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class CreateTurnoWithoutUrgency implements Task {

    private static final Logger logger = LoggerFactory.getLogger(CreateTurnoWithoutUrgency.class);

    private final long idCard;
    private final String fullName;

    public CreateTurnoWithoutUrgency(long idCard, String fullName) {
        this.idCard = idCard;
        this.fullName = fullName;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Creating appointment WITHOUT priority for patient: {} - {}", idCard, fullName);

        JsonObject body = new JsonObject();
        body.addProperty(TestConstants.Payload.ID_CARD, idCard);
        body.addProperty(TestConstants.Payload.FULL_NAME, fullName);

        String token = RestContext.getAuthToken();
        RestContext.setLastResponse(
                RestAssured.given()
                        .contentType(TestConstants.Api.CONTENT_TYPE_JSON)
                        .header("Authorization", "Bearer " + token)
                        .body(body.toString())
                        .post(TestConstants.Api.APPOINTMENTS_ENDPOINT)
        );

        logger.info("POST {} executed WITHOUT priority field", TestConstants.Api.APPOINTMENTS_ENDPOINT);
    }

    public static CreateTurnoWithoutUrgency forPatient(long idCard, String fullName) {
        return new CreateTurnoWithoutUrgency(idCard, fullName);
    }
}
