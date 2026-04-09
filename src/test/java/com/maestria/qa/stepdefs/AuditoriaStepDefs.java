package com.maestria.qa.stepdefs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.actors.ApiTester;
import com.maestria.qa.tasks.GetAuditLog;

import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;

public class AuditoriaStepDefs {

    private static final Logger logger = LoggerFactory.getLogger(AuditoriaStepDefs.class);

    private Actor actor;

    @When("queries the audit logs")
    public void queriesAuditLogs() {
        actor = ApiTester.withDefaultName();
        logger.info("Querying audit logs");
        actor.attemptsTo(GetAuditLog.fromApi());
    }
}
