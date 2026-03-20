package com.maestria.qa.runners;

import org.junit.runner.RunWith;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.maestria.qa.stepdefs",
    plugin = {
        "pretty",
        "junit:target/cucumber-reports/cucumber.xml",
        "json:target/cucumber-reports/cucumber.json",
        "html:target/cucumber-reports/cucumber.html"
    },
    tags = "not @wip",
    monochrome = false,
    dryRun = false
)
public class CucumberRunner {
    // Cucumber runner configuration
}
