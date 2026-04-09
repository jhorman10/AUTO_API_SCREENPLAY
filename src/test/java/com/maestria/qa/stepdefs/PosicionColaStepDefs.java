package com.maestria.qa.stepdefs;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.actors.ApiTester;
import com.maestria.qa.questions.QueuePosition;
import com.maestria.qa.tasks.GetQueuePosition;
import com.maestria.qa.utils.RestContext;
import com.maestria.qa.utils.TestConstants;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;

public class PosicionColaStepDefs {

    private static final Logger logger = LoggerFactory.getLogger(PosicionColaStepDefs.class);

    private Actor actor;

    @When("queries the queue position for idCard {long}")
    public void queriesQueuePositionForIdCard(long idCard) {
        actor = ApiTester.withDefaultName();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        actor.attemptsTo(GetQueuePosition.forIdCard(String.valueOf(idCard)));
    }

    @And("the queue status is {string}")
    public void queueStatusIs(String expectedStatus) {
        actor = ApiTester.withDefaultName();
        String status = RestContext.getLastResponse().jsonPath().getString(TestConstants.Payload.STATUS);
        actor.should(seeThat(actor1 -> status, is(expectedStatus)));
        logger.info("Queue status verified: {}", expectedStatus);
    }

    @And("the position is a number greater than {int}")
    public void positionIsGreaterThan(int min) {
        actor = ApiTester.withDefaultName();
        actor.should(seeThat(QueuePosition.fromLastResponse(), greaterThan(min)));
    }

    @And("the position is {int}")
    public void positionIs(int expectedPosition) {
        actor = ApiTester.withDefaultName();
        actor.should(seeThat(QueuePosition.fromLastResponse(), is(expectedPosition)));
    }

    @And("the total in queue is at least {int}")
    public void totalInQueueIsAtLeast(int min) {
        Integer total = RestContext.getLastResponse().jsonPath().get(TestConstants.Payload.TOTAL);
        actor = ApiTester.withDefaultName();
        actor.should(seeThat(actor1 -> total, greaterThanOrEqualTo(min)));
    }
}
