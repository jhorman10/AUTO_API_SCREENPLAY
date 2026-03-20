package com.maestria.qa.stepdefs;

import com.maestria.qa.actors.ApiTester;
import com.maestria.qa.tasks.*;
import com.maestria.qa.questions.*;
import com.maestria.qa.utils.ConversionDataFactory;
import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.questions.LastResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.serenitybdd.screenplay.GivenWhenThen.*;
import static org.hamcrest.CoreMatchers.*;

public class ConversionStepDefinitions {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversionStepDefinitions.class);
    
    private Actor actor;
    private String currentConversionId;
    
    @Before
    public void setupStage() {
        logger.info("Setting up test stage");
        ApiTester.prepareStage();
    }
    
    @After
    public void cleanupStage() {
        logger.info("Cleaning up test stage");
        ApiTester.cleanUpStage();
    }
    
    @Dado("que el ApiTester está listo para crear conversiones")
    public void setupApiTester() {
        actor = ApiTester.withDefaultName();
        logger.info("ApiTester is ready to create conversions");
    }
    
    @Cuando("crea una conversión con tipo {string} y valor {double}")
    public void createsConversionWithTypeAndValue(String tipo, double valor) {
        var conversionData = ConversionDataFactory.generateConversionWithTypeAndValue(tipo, valor);
        actor.attemptsTo(CreateConversion.withData(conversionData));
    }
    
    @Cuando("intenta crear una conversión sin proporcionar el user_id")
    public void createsConversionWithoutUserId() {
        var conversionData = new com.google.gson.JsonObject();
        conversionData.addProperty("tipo", "venta");
        conversionData.addProperty("valor", 100.0);
        actor.attemptsTo(CreateConversion.withData(conversionData));
    }
    
    @Entonces("la conversión se crea exitosamente con status {int}")
    public void verifyConversionCreatedSuccessfully(int expectedStatus) {
        actor.should(seeThat(ResponseStatus.code(), is(expectedStatus)));
        currentConversionId = actor.asksFor(ConversionId.fromLastResponse());
        logger.info("Conversion created with ID: {}", currentConversionId);
    }
    
    @Entonces("recibe error de validación con status {int}")
    public void verifyValidationError(int expectedStatus) {
        actor.should(seeThat(ResponseStatus.code(), is(expectedStatus)));
        logger.info("Validation error received with status: {}", expectedStatus);
    }
    
    @Entonces("el mensaje de error menciona el campo faltante")
    public void verifyErrorMessageContainsMissingField() {
        String responseBody = LastResponse.received().asString();
        logger.info("Error response: {}", responseBody);
        assert responseBody.contains("user_id") || responseBody.contains("field") || responseBody.contains("required");
    }
    
    @Cuando("obtiene la lista de conversiones")
    public void fetchesAllConversions() {
        actor.attemptsTo(FetchAllConversions.fromTheApi());
    }
    
    @Entonces("la lista contiene al menos {int} conversión|conversiones")
    public void verifyListContainsConversions(int minimumCount) {
        actor.should(seeThat(ConversionList.size(), greaterThanOrEqualTo(minimumCount)));
    }
    
    @Cuando("obtiene la conversión por su id creado")
    public void fetchesConversionById() {
        actor.attemptsTo(FetchConversionById.withId(currentConversionId));
    }
    
    @Entonces("los datos retornados son consistentes con los enviados")
    public void verifyConversionDataConsistency() {
        String uid = LastResponse.received().jsonPath().get("uid");
        assert uid.equals(currentConversionId) : "UID mismatch";
        logger.info("Conversion data is consistent");
    }
    
    @Cuando("actualiza la conversión a estado {string}")
    public void updatesConversionStatus(String newStatus) {
        var updateData = new com.google.gson.JsonObject();
        updateData.addProperty("estado", newStatus);
        actor.attemptsTo(UpdateConversion.withId(currentConversionId).andData(updateData));
    }
    
    @Entonces("la actualización es exitosa con status {int}")
    public void verifyUpdateSuccessful(int expectedStatus) {
        actor.should(seeThat(ResponseStatus.code(), is(expectedStatus)));
    }
    
    @Entonces("el estado de la conversión es {string}")
    public void verifyConversionStatus(String expectedStatus) {
        String actualStatus = actor.asksFor(ConversionStatus.fromLastResponse());
        assert actualStatus.equals(expectedStatus) : "Status mismatch: expected " + expectedStatus + ", got " + actualStatus;
        logger.info("Conversion status verified: {}", actualStatus);
    }
    
    @Cuando("elimina la conversión")
    public void deletesConversion() {
        actor.attemptsTo(DeleteConversion.withId(currentConversionId));
    }
    
    @Entonces("la eliminación es exitosa con status {int}")
    public void verifyDeleteSuccessful(int expectedStatus) {
        actor.should(seeThat(ResponseStatus.code(), is(expectedStatus)));
        logger.info("Conversion {} deleted successfully", currentConversionId);
    }
}
