package com.rh.conges.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration for scheduled tasks
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // The @EnableScheduling annotation is sufficient for basic configuration
    // For more complex scheduling needs, you can add custom beans here
}

