package com.maestria.qa.runners;

import org.junit.runner.RunWith;

import com.maestria.qa.utils.TestConstants;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
    features = TestConstants.Cucumber.FEATURES_PATH,
    glue = TestConstants.Cucumber.GLUE_PATH,
    plugin = {
        TestConstants.Cucumber.PRETTY_PLUGIN,
        TestConstants.Cucumber.JUNIT_PLUGIN,
        TestConstants.Cucumber.JSON_PLUGIN,
        TestConstants.Cucumber.HTML_PLUGIN
    },
    tags = TestConstants.Cucumber.DEFAULT_TAG_FILTER,
    monochrome = false,
    dryRun = false
)
public class CucumberRunner {
    // Cucumber runner configuration
}
