package com.maestria.qa.actors;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.Cast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ApiTester - Actor que realiza pruebas contra la API REST
 * 
 * En el patrón Screenplay, el Actor es la entidad que realiza Tareas
 * e interactúa con la API bajo prueba.
 * 
 * Ejemplo de uso:
 *   Actor tester = ApiTester.withDefaultName();
 *   tester.attemptsTo(SomeTask.withParameters(...));
 *   tester.should(seeThat(SomeQuestion.check(...), is(expectedValue)));
 */
public class ApiTester {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiTester.class);
    
    /**
     * Obtiene o crea el actor con el nombre por defecto "ApiTester"
     * 
     * @return Actor llamado "ApiTester"
     */
    public static Actor withDefaultName() {
        logger.info("Creating or retrieving actor 'ApiTester'");
        return OnStage.theActorCalled("ApiTester");
    }
    
    /**
     * Obtiene o crea un actor con un nombre específico
     * 
     * @param name El nombre del actor
     * @return Actor con el nombre especificado
     */
    public static Actor named(String name) {
        logger.info("Creating or retrieving actor '{}'", name);
        return OnStage.theActorCalled(name);
    }
    
    /**
     * Prepara el escenario antes de cada test
     * Inicializa el stage de Serenity con un Cast vacío
     */
    public static void prepareStage() {
        logger.info("Preparing stage for test execution");
        OnStage.setTheStage(new Cast());
    }
    
    /**
     * Limpia el escenario después de cada test
     * Libera recursos y finaliza el stage de Serenity
     */
    public static void cleanUpStage() {
        logger.info("Cleaning up stage after test execution");
        OnStage.drawTheCurtain();
    }
}
