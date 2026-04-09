package com.maestria.qa.stepdefs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.actors.ApiTester;
import com.maestria.qa.utils.RestContext;
import com.maestria.qa.utils.TestConstants;

import io.cucumber.java.en.And;
import net.serenitybdd.screenplay.Actor;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;

public class AsignacionMedicoStepDefs {

    private static final Logger logger = LoggerFactory.getLogger(AsignacionMedicoStepDefs.class);

    private Actor actor;

    @And("waits briefly for potential assignment")
    public void waitsBrieflyForPotentialAssignment() {
        logger.info("Waiting briefly (3s) for potential assignment processing...");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Wait interrupted");
        }
    }

    @And("the doctor name is null")
    public void doctorNameIsNull() {
        actor = ApiTester.withDefaultName();
        String doctorName = RestContext.getLastResponse().jsonPath()
                .getString("[0]." + TestConstants.Payload.DOCTOR_NAME);
        actor.should(seeThat(actor1 -> doctorName, is(nullValue())));
        logger.info("Doctor name is null (no assignment)");
    }

    @And("the office is null")
    public void officeIsNull() {
        actor = ApiTester.withDefaultName();
        String office = RestContext.getLastResponse().jsonPath()
                .getString("[0]." + TestConstants.Payload.OFFICE);
        actor.should(seeThat(actor1 -> office, is(nullValue())));
        logger.info("Office is null (no assignment)");
    }
}
