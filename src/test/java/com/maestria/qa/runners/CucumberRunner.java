package com.maestria.qa.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
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
