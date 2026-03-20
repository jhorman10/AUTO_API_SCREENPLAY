package com.maestria.qa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.maestria.qa.utils.RestContext;
import com.maestria.qa.utils.TestConstants;

import io.restassured.RestAssured;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class CreateTurno implements Task {

    private static final Logger logger = LoggerFactory.getLogger(CreateTurno.class);

    private final long cedula;
    private final String nombre;
    private final String priority;

    public CreateTurno(long cedula, String nombre, String priority) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.priority = priority;
    }

    public CreateTurno(long cedula, String nombre) {
        this(cedula, nombre, TestConstants.Defaults.PRIORITY);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Creating turno for patient: {} - {}", cedula, nombre);

        JsonObject body = new JsonObject();
        body.addProperty(TestConstants.Payload.CEDULA, cedula);
        body.addProperty(TestConstants.Payload.NOMBRE, nombre);
        body.addProperty(TestConstants.Payload.PRIORITY, priority);

        RestContext.setLastResponse(
            RestAssured.given()
                .contentType(TestConstants.Api.CONTENT_TYPE_JSON)
                .body(body.toString())
                .post(TestConstants.Api.TURNOS_ENDPOINT)
        );

        logger.info("POST {} executed", TestConstants.Api.TURNOS_ENDPOINT);
    }

    public static CreateTurno forPatient(long cedula, String nombre) {
        return new CreateTurno(cedula, nombre, TestConstants.Defaults.PRIORITY);
    }

    public static CreateTurno forPatientWithPriority(long cedula, String nombre, String priority) {
        return new CreateTurno(cedula, nombre, priority);
    }
}
