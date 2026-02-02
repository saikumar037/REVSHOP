package com.revshop.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class LoggerConfig {
    private static final Logger logger = LogManager.getLogger(LoggerConfig.class);

    public static void initialize() {
        try {
            // Log4j2 will auto-configure from log4j2.xml in resources
            logger.info("Log4j2 initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
            // Fallback to basic logging
            Configurator.initialize(null, "log4j2.xml");
        }
    }
}