package com.maestria.qa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.maestria.qa.utils.RestContext;
import com.maestria.qa.utils.TestConstants;

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
        this(email, password, TestConstants.Defaults.USER_NAME, TestConstants.Defaults.ROLE);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Registering new user with email: {}", email);

        JsonObject body = new JsonObject();
        body.addProperty(TestConstants.Payload.EMAIL, email);
        body.addProperty(TestConstants.Payload.PASSWORD, password);
        body.addProperty(TestConstants.Payload.NOMBRE, nombre);
        body.addProperty(TestConstants.Payload.ROL, rol);

        RestContext.setLastResponse(
            RestAssured.given()
                .contentType(TestConstants.Api.CONTENT_TYPE_JSON)
                .body(body.toString())
                .post(TestConstants.Api.SIGN_UP_ENDPOINT)
        );

        logger.info("POST {} executed", TestConstants.Api.SIGN_UP_ENDPOINT);
    }

    public static SignUp withCredentials(String email, String password) {
        return new SignUp(
            email,
            password,
            email.split(TestConstants.Email.ADDRESS_SEPARATOR)[0],
            TestConstants.Defaults.ROLE
        );
    }

    public static SignUp withFullDetails(String email, String password, String nombre, String rol) {
        return new SignUp(email, password, nombre, rol);
    }
}
