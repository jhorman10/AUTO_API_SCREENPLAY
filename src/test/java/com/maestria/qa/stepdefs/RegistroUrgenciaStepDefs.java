package com.maestria.qa.stepdefs;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.actors.ApiTester;
import com.maestria.qa.questions.ErrorMessage;
import com.maestria.qa.questions.ResponseCode;
import com.maestria.qa.questions.TurnoState;
import com.maestria.qa.questions.TurnoUrgency;
import com.maestria.qa.tasks.CancelActiveAppointment;
import com.maestria.qa.tasks.CreateTurnoWithUrgency;
import com.maestria.qa.tasks.CreateTurnoWithoutUrgency;
import com.maestria.qa.tasks.GetTurnoById;
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

public class RegistroUrgenciaStepDefs {

    private static final Logger logger = LoggerFactory.getLogger(RegistroUrgenciaStepDefs.class);

    private Actor actor;

    @Before("@hu03")
    public void setupStage() {
        logger.info("Setting up HU03 test stage");
        ApiTester.prepareStage();
        ApiConfig.getBaseUrl();
        RestContext.clear();
    }

    @After("@hu03")
    public void cleanupStage() {
        logger.info("Cleaning up HU03 test stage");
        RestContext.clear();
        ApiTester.cleanUpStage();
    }

    @Given("the API is configured for HU03 tests")
    public void apiConfiguredForHu03() {
        logger.info("API configured at {} for HU03 tests", ApiConfig.getBaseUrl());
    }

    @Given("a receptionist is authenticated")
    public void receptionistIsAuthenticated() {
        actor = ApiTester.withDefaultName();
        logger.info("Authenticating receptionist via Firebase signIn");
        actor.attemptsTo(SignUp.withTestCredentials());
    }

    @When("creates an appointment with idCard {long} name {string} and priority {string}")
    public void createsAppointmentWithPriority(long idCard, String fullName, String priority) {
        logger.info("Creating appointment: idCard={}, fullName={}, priority={}", idCard, fullName, priority);
        actor.attemptsTo(CancelActiveAppointment.forIdCard(String.valueOf(idCard)));
        actor.attemptsTo(CreateTurnoWithUrgency.forPatient(idCard, fullName, priority));
    }

    @When("attempts to create an appointment with idCard {long} name {string} without priority")
    public void createsAppointmentWithoutPriority(long idCard, String fullName) {
        logger.info("Creating appointment WITHOUT priority: idCard={}, fullName={}", idCard, fullName);
        actor.attemptsTo(CancelActiveAppointment.forIdCard(String.valueOf(idCard)));
        actor.attemptsTo(CreateTurnoWithoutUrgency.forPatient(idCard, fullName));
    }

    @Then("the appointment is accepted with status {int}")
    public void appointmentAcceptedWithStatus(int expectedStatus) {
        actor.should(seeThat(ResponseCode.fromLastResponse(), is(expectedStatus)));
        logger.info("Appointment accepted with status: {}", expectedStatus);
    }

    @Then("receives error with status {int}")
    public void receivesErrorWithStatus(int expectedStatus) {
        actor.should(seeThat(ResponseCode.fromLastResponse(), is(expectedStatus)));
        logger.info("Error received with status: {}", expectedStatus);
    }

    @When("queries the appointment by idCard {long}")
    public void queriesAppointmentByIdCard(long idCard) {
        logger.info("Querying appointment by idCard: {}", idCard);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        actor.attemptsTo(GetTurnoById.withId(String.valueOf(idCard)));
    }

    @Then("the response status is {int}")
    public void responseStatusIs(int expectedStatus) {
        if (actor == null) {
            actor = ApiTester.withDefaultName();
        }
        actor.should(seeThat(ResponseCode.fromLastResponse(), is(expectedStatus)));
        logger.info("Response status verified: {}", expectedStatus);
    }

    @And("the appointment status is {string}")
    public void appointmentStatusIs(String expectedStatus) {
        if (actor == null) {
            actor = ApiTester.withDefaultName();
        }
        actor.should(seeThat(TurnoState.fromLastResponse(), is(expectedStatus)));
        logger.info("Appointment status verified: {}", expectedStatus);
    }

    @And("the appointment priority is {string}")
    public void appointmentPriorityIs(String expectedPriority) {
        if (actor == null) {
            actor = ApiTester.withDefaultName();
        }
        actor.should(seeThat(TurnoUrgency.fromLastResponse(), is(expectedPriority)));
        logger.info("Appointment priority verified: {}", expectedPriority);
    }

    @And("the error message contains {string}")
    public void errorMessageContains(String expectedFragment) {
        if (actor == null) {
            actor = ApiTester.withDefaultName();
        }
        actor.should(seeThat(ErrorMessage.fromResponse(), containsString(expectedFragment)));
        logger.info("Error message contains: {}", expectedFragment);
    }
}
