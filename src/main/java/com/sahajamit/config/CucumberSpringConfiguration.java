package com.sahajamit.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {TestConfig.class})
@CucumberContextConfiguration
public class CucumberSpringConfiguration {
}
