package com.maestria.qa.actors;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.Cast;
import net.serenitybdd.screenplay.actors.OnStage;

public class ApiTester {

    public static Actor withDefaultName() {
        return OnStage.theActorCalled("ApiTester");
    }

    public static Actor named(String name) {
        return OnStage.theActorCalled(name);
    }

    @SuppressWarnings("unchecked")
    public static void prepareStage() {
        OnStage.setTheStage(new Cast());
    }

    public static void cleanUpStage() {
        OnStage.drawTheCurtain();
    }
}
