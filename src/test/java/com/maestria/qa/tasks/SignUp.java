package com.maestria.qa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.maestria.qa.utils.RestContext;

import io.restassured.RestAssured;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class SignUp implements Task {

    private static final Logger logger = LoggerFactory.getLogger(SignUp.class);

    private final String email;
    private final String password;
    private final String nombre;
    private final String rol;

    public SignUp(String email, String password, String nombre, String rol) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.rol = rol;
    }

    public SignUp(String email, String password) {
        this(email, password, "QA Tester", "empleado");
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Registering new user with email: {}", email);

        JsonObject body = new JsonObject();
        body.addProperty("email", email);
        body.addProperty("password", password);
        body.addProperty("nombre", nombre);
        body.addProperty("rol", rol);

        RestContext.setLastResponse(
            RestAssured.given()
                .contentType("application/json")
                .body(body.toString())
                .post("/auth/signUp")
        );

        logger.info("POST /auth/signUp executed");
    }

    public static SignUp withCredentials(String email, String password) {
        return new SignUp(email, password, email.split("@")[0], "user");
    }

    public static SignUp withFullDetails(String email, String password, String nombre, String rol) {
        return new SignUp(email, password, nombre, rol);
    }
}
