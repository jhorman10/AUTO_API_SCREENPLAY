package com.maestria.qa.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.thucydides.core.reports.html.HtmlAggregateStoryReporter;

public final class SerenityReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(SerenityReportGenerator.class);

    private SerenityReportGenerator() {
    }

    public static void main(String[] args) {
        Path serenityDirectory = Path.of(TestConstants.Reports.SERENITY_RESULTS_DIRECTORY);

        if (Files.notExists(serenityDirectory)) {
            logger.warn("No Serenity results were found in {}", serenityDirectory.toAbsolutePath());
            return;
        }

        try {
            Files.createDirectories(serenityDirectory);

            File outputDirectory = serenityDirectory.toFile();
            HtmlAggregateStoryReporter reporter = new HtmlAggregateStoryReporter(
                TestConstants.Defaults.SERENITY_PROJECT_NAME
            );

            reporter.setSourceDirectory(outputDirectory);
            reporter.setOutputDirectory(outputDirectory);
            reporter.setGenerateTestOutcomeReports();
            reporter.generateReportsForTestResultsFrom(outputDirectory);

            logger.info("Serenity HTML report generated at {}", Path.of(TestConstants.Reports.SERENITY_INDEX_REPORT).toAbsolutePath());
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to generate Serenity HTML report", exception);
        }
    }
}