package com.maestria.qa.stepdefs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.actors.ApiTester;
import com.maestria.qa.questions.JwtToken;
import com.maestria.qa.questions.ResponseCode;
import com.maestria.qa.questions.TurnosList;
import com.maestria.qa.questions.UserProfile;
import com.maestria.qa.tasks.CreateTurno;
import com.maestria.qa.tasks.GetAllTurnos;
import com.maestria.qa.tasks.GetDashboardHistory;
import com.maestria.qa.tasks.GetMe;
import com.maestria.qa.tasks.GetTurnosByCedula;
import com.maestria.qa.tasks.SignIn;
import com.maestria.qa.tasks.SignOut;
import com.maestria.qa.tasks.SignUp;
import com.maestria.qa.utils.RestContext;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import net.serenitybdd.screenplay.Actor;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;

public class TurnosAuthStepDefinitions {

    private static final Logger logger = LoggerFactory.getLogger(TurnosAuthStepDefinitions.class);

    private Actor actor;
    private String currentToken;
    private String currentEmail;
    private String currentPassword;
    private long currentCedula;

    @Before
    public void setupStage() {
        logger.info("Setting up test stage");
        RestContext.clear();
    }

    @After
    public void cleanupStage() {
        logger.info("Cleaning up test stage");
        RestContext.clear();
    }

    @Given("the API is configured at http://localhost:3000")
    public void apiConfigured() {
        logger.info("API configured");
    }

    @When("a new user registers with email {string} and password {string}")
    public void registersNewUser(String email, String password) {
        actor = ApiTester.withDefaultName();
        currentEmail = email;
        currentPassword = password;
        logger.info("Registering user: {}", email);
        actor.attemptsTo(SignUp.withCredentials(email, password));
    }

    @Then("the registration is successful with status {int}")
    public void verifySignupSuccess(int expectedStatus) {
        actor.should(seeThat(ResponseCode.fromLastResponse(), is(expectedStatus)));
        logger.info("Signup successful with status: {}", expectedStatus);
    }

    @And("obtains a valid JWT token")
    public void extractsJwtToken() {
        currentToken = actor.asksFor(JwtToken.fromResponse());
        assert currentToken != null && !currentToken.isEmpty();
        logger.info("JWT token extracted successfully");
    }

    @And("signs in with email {string} and password {string}")
    public void signsIn(String email, String password) {
        logger.info("Signing in with email: {}", email);
        actor.attemptsTo(SignIn.withCredentials(email, password));
    }

    @Then("the login is successful with status {int}")
    public void verifySigninSuccess(int expectedStatus) {
        actor.should(seeThat(ResponseCode.fromLastResponse(), is(expectedStatus)));
        currentToken = actor.asksFor(JwtToken.fromResponse());
        assert currentToken != null;
        logger.info("Login successful with status: {}", expectedStatus);
    }

    @And("retrieves user profile with token")
    public void consultUserProfile() {
        logger.info("Consulting user profile with token");
        actor.attemptsTo(GetMe.withToken(currentToken));
    }

    @Then("obtains user data with status {int}")
    public void verifyUserProfileSuccess(int expectedStatus) {
        actor.should(seeThat(ResponseCode.fromLastResponse(), is(expectedStatus)));
        String email = actor.asksFor(UserProfile.email());
        assert email != null;
        logger.info("User profile retrieved successfully");
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

    @And("retrieves dashboard history with token")
    public void fetchesDashboardHistory() {
        logger.info("Fetching dashboard history");
        actor.attemptsTo(GetDashboardHistory.withToken(currentToken));
    }

    @Then("obtains dashboard history with status {int}")
    public void verifyDashboardHistory(int expectedStatus) {
        actor.should(seeThat(ResponseCode.fromLastResponse(), is(expectedStatus)));
        logger.info("Dashboard history retrieved");
    }

    @And("signs out")
    public void signsOut() {
        logger.info("Signing out");
        actor.attemptsTo(SignOut.withToken(currentToken));
    }

    @Then("the session closes successfully with status {int}")
    public void verifySignoutSuccess(int expectedStatus) {
        actor.should(seeThat(ResponseCode.fromLastResponse(), is(expectedStatus)));
        logger.info("Signout successful");
    }

    @When("attempts to fetch user profile without token")
    public void triesToFetchProfileWithoutToken() {
        actor = ApiTester.withDefaultName();
        actor.attemptsTo(GetMe.withToken(""));
    }

    @Then("receives unauthorized error with status {int}")
    public void verifyUnauthorizedError(int expectedStatus) {
        actor.should(seeThat(ResponseCode.fromLastResponse(), is(expectedStatus)));
        logger.info("Unauthorized error verified");
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

    @When("attempts to fetch history without token")
    public void triesToFetchHistoryWithoutToken() {
        actor = ApiTester.withDefaultName();
        actor.attemptsTo(GetDashboardHistory.withToken(""));
    }
}
