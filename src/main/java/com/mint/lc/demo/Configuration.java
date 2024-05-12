package com.mint.lc.demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class Configuration {

    public static void loadLoggingProperties() {
        try (InputStream is = Configuration.class.getResourceAsStream("/logger.properties")) {
            if (is != null) {
                LogManager.getLogManager().readConfiguration(is);
            } else {
                System.err.println("Failed to load logging properties file.");
            }
        } catch (IOException e) {
            System.err.println("Error loading logging properties: " + e.getMessage());
        }
    }
}