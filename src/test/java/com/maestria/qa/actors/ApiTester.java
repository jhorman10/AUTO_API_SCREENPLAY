package com.maestria.qa.actors;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.Cast;

/**
 * ApiTester - Actor que realiza pruebas contra la API REST
 * 
 * En el patrón Screenplay, el Actor es la entidad que realiza Tareas
 * e interactúa con el sistema bajo prueba (en este caso, la API).
 */
public class ApiTester {
    
    /**
     * Obtiene o crea el actor "ApiTester"
     */
    public static Actor withDefaultName() {
        return OnStage.theActorCalled("ApiTester");
    }
    
    /**
     * Obtiene o crea un actor con un nombre específico
     */
    public static Actor named(String name) {
        return OnStage.theActorCalled(name);
    }
    
    /**
     * Prepara el escenario antes de cada test
     */
    public static void prepareStage() {
        OnStage.setTheStage(new Cast());
    }
    
    /**
     * Limpia el escenario después de cada test
     */
    public static void cleanUpStage() {
        OnStage.drawTheCurtain();
    }
}
