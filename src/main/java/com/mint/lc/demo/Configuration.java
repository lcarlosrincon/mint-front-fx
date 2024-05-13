package com.mint.lc.demo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.LogManager;

public class Configuration {

    public static void loadProperties() {
        try {
            Properties properties = new Properties();
            try (InputStream is = Configuration.class.getResourceAsStream("/config.properties")) {
                properties.load(is);
            }
            for (String propertyName : properties.stringPropertyNames()) {
                String propertyValue = properties.getProperty(propertyName);
                System.setProperty(propertyName, propertyValue);
            }

            String apiUrl = System.getProperty("api.url");
            System.out.println("API URL from system properties: " + apiUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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