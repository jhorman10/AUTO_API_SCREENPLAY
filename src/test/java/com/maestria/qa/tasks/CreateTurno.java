package com.maestria.qa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.maestria.qa.utils.RestContext;

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
        this(cedula, nombre, "media");
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Creating turno for patient: {} - {}", cedula, nombre);

        JsonObject body = new JsonObject();
        body.addProperty("cedula", cedula);
        body.addProperty("nombre", nombre);
        body.addProperty("priority", priority);

        RestContext.setLastResponse(
            RestAssured.given()
                .contentType("application/json")
                .body(body.toString())
                .post("/turnos")
        );

        logger.info("POST /turnos executed");
    }

    public static CreateTurno forPatient(long cedula, String nombre) {
        return new CreateTurno(cedula, nombre, "media");
    }

    public static CreateTurno forPatientWithPriority(long cedula, String nombre, String priority) {
        return new CreateTurno(cedula, nombre, priority);
    }
}
