package com.maestria.qa.stepdefs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.actors.ApiTester;
import com.maestria.qa.questions.ResponseCode;
import com.maestria.qa.questions.TurnosList;
import com.maestria.qa.tasks.CreateTurno;
import com.maestria.qa.tasks.GetAllTurnos;
import com.maestria.qa.tasks.GetTurnosByCedula;
import com.maestria.qa.tasks.SignUp;
import com.maestria.qa.utils.ApiConfig;
import com.maestria.qa.utils.RestContext;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;

public class TurnosAuthStepDefinitions {

    private static final Logger logger = LoggerFactory.getLogger(TurnosAuthStepDefinitions.class);

    private Actor actor;
    private String currentEmail;
    private String currentPassword;
    private long currentCedula;

    @Before
    public void setupStage() {
        logger.info("Setting up test stage");
        ApiTester.prepareStage();
        ApiConfig.getBaseUrl();
        RestContext.clear();
    }

    @After
    public void cleanupStage() {
        logger.info("Cleaning up test stage");
        RestContext.clear();
        ApiTester.cleanUpStage();
    }

    @Given("the API is configured")
    public void apiConfigured() {
        logger.info("API configured at {}", ApiConfig.getBaseUrl());
    }

    @When("a new user registers with email {string} and password {string}")
    public void registersNewUser(String email, String password) {
        actor = ApiTester.withDefaultName();
        currentEmail = buildUniqueEmail(email);
        currentPassword = password;
        logger.info("Registering user: {}", currentEmail);
        actor.attemptsTo(SignUp.withCredentials(currentEmail, password));
    }

    @Then("the registration is successful with status {int}")
    public void verifySignupSuccess(int expectedStatus) {
        actor.should(seeThat(ResponseCode.fromLastResponse(), is(expectedStatus)));
        logger.info("Signup successful with status: {}", expectedStatus);
    }

    @And("creates a turno with cedula {long} name {string} and priority {string}")
    public void createsTurno(long cedula, String nombre, String priority) {
        currentCedula = cedula;
        logger.info("Creating turno for cedula: {} - {}", cedula, nombre);
        actor.attemptsTo(CreateTurno.forPatientWithPriority(cedula, nombre, priority));
    }

    @Then("the turno is created successfully with status {int}")
    public void verifyTurnoCreated(int expectedStatus) {
        actor.should(seeThat(ResponseCode.fromLastResponse(), is(expectedStatus)));
        logger.info("Turno created successfully with status: {}", expectedStatus);
    }

    @And("retrieves the list of all turnos")
    public void fetchesAllTurnos() {
        logger.info("Fetching all turnos");
        actor.attemptsTo(GetAllTurnos.fromTheApi());
    }

    @Then("the list contains at least {int} turno with status {int}")
    public void verifyTurnosList(int minimumCount, int expectedStatus) {
        actor.should(seeThat(ResponseCode.fromLastResponse(), is(expectedStatus)));
        actor.should(seeThat(TurnosList.size(), greaterThanOrEqualTo(minimumCount)));
        logger.info("Turnos list verified");
    }

    @And("retrieves turnos for patient with cedula {long}")
    public void fetchesTurnosByCedula(long cedula) {
        logger.info("Fetching turnos for cedula: {}", cedula);
        actor.attemptsTo(GetTurnosByCedula.forPatient(cedula));
    }

    @Then("finds the patient turno with status {int}")
    public void verifyPatientTurnosFound(int expectedStatus) {
        actor.should(seeThat(ResponseCode.fromLastResponse(), is(expectedStatus)));
        logger.info("Patient turnos found");
    }

    @When("attempts to create turno with invalid cedula {string} and name {string}")
    public void triesToCreateTurnoWithInvalidCedula(String invalidCedula, String nombre) {
        actor = ApiTester.withDefaultName();
        try {
            long cedula = Long.parseLong(invalidCedula);
            actor.attemptsTo(CreateTurno.forPatient(cedula, nombre));
        } catch (NumberFormatException e) {
            actor.attemptsTo(CreateTurno.forPatient(0L, nombre));
            logger.info("Invalid cedula format used: {}", invalidCedula);
        }
    }

    @Then("receives validation error with status {int}")
    public void verifyValidationError(int expectedStatus) {
        actor.should(seeThat(ResponseCode.fromLastResponse(), is(expectedStatus)));
        logger.info("Validation error verified");
    }

    private String buildUniqueEmail(String baseEmail) {
        String[] parts = baseEmail.split("@", 2);
        if (parts.length != 2) {
            return baseEmail;
        }
        return parts[0] + "+" + System.currentTimeMillis() + "@" + parts[1];
    }
}
