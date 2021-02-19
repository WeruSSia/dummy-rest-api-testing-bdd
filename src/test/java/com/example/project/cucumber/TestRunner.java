package com.example.project.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(features = {"src/test/resources/cucumberScenarios"}, plugin = {"pretty"}, glue={"com.example.project.cucumber"})
public class TestRunner {
}
