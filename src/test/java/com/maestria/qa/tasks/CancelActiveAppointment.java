package com.maestria.qa.tasks;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

public class CancelActiveAppointment implements Task {

    private static final Logger logger = LoggerFactory.getLogger(CancelActiveAppointment.class);

    private final String idCard;

    public CancelActiveAppointment(String idCard) {
        this.idCard = idCard;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Cleaning up waiting appointments for idCard: {} via MongoDB", idCard);
        try {
            String mongoCmd = String.format(
                    "db.getSiblingDB('appointments_db').appointments.deleteMany({idCard: %s, status: 'waiting'})",
                    idCard);
            ProcessBuilder pb = new ProcessBuilder(
                    "docker", "exec", "P1_mongodb",
                    "mongosh", "-u", "sofka_admin", "-p", "sofka_secure_pass_456",
                    "--authenticationDatabase", "admin", "--quiet", "--eval", mongoCmd);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info("MongoDB cleanup: {}", line);
            }
            int exitCode = process.waitFor();
            logger.info("MongoDB cleanup exit code: {}", exitCode);
        } catch (Exception e) {
            logger.warn("MongoDB cleanup failed for idCard {}: {}", idCard, e.getMessage());
        }
    }

    public static CancelActiveAppointment forIdCard(String idCard) {
        return new CancelActiveAppointment(idCard);
    }
}
