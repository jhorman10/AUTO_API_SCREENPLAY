package com.maestria.qa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.utils.RestContext;

import io.restassured.RestAssured;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class GetTurnosByCedula implements Task {

    private static final Logger logger = LoggerFactory.getLogger(GetTurnosByCedula.class);

    private final long cedula;

    public GetTurnosByCedula(long cedula) {
        this.cedula = cedula;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Fetching turnos for cedula: {}", cedula);

        RestContext.setLastResponse(
            RestAssured.given()
                .get("/turnos/" + cedula)
        );

        logger.info("GET /turnos/{} executed", cedula);
    }

    public static GetTurnosByCedula forPatient(long cedula) {
        return new GetTurnosByCedula(cedula);
    }
}
