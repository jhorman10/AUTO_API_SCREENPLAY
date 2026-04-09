package com.maestria.qa.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.utils.RestContext;
import com.maestria.qa.utils.TestConstants;

import io.restassured.RestAssured;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class GetAuditLog implements Task {

    private static final Logger logger = LoggerFactory.getLogger(GetAuditLog.class);

    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Fetching audit logs");

        String token = RestContext.getAuthToken();
        RestContext.setLastResponse(
                RestAssured.given()
                        .header("Authorization", "Bearer " + token)
                        .get(TestConstants.Api.AUDIT_LOGS_ENDPOINT)
        );

        logger.info("GET {} executed", TestConstants.Api.AUDIT_LOGS_ENDPOINT);
    }

    public static GetAuditLog fromApi() {
        return new GetAuditLog();
    }
}
