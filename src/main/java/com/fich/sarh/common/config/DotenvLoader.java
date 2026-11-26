package com.fich.sarh.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Carga automáticamente variables de entorno desde un archivo .env si existe,
 * poniéndolas a disposición de Spring Boot tanto en System Properties como en el Environment.
 */
public class DotenvLoader implements EnvironmentPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(DotenvLoader.class);
    private static final String PROPERTY_SOURCE_NAME = "dotenvProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> props = loadProperties();
        if (!props.isEmpty()) {
            environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, props));
        }
    }

    /**
     * Permite cargar las variables explícitamente antes de que SpringApplication.run() se invoque.
     */
    public static void load() {
        loadProperties();
    }

    private static Map<String, Object> loadProperties() {
        Map<String, Object> props = new HashMap<>();
        File envFile = findEnvFile();
        if (envFile == null || !envFile.exists() || !envFile.isFile()) {
            return props;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(envFile, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int equalsIndex = line.indexOf('=');
                if (equalsIndex <= 0) {
                    continue;
                }
                String key = line.substring(0, equalsIndex).trim();
                String value = line.substring(equalsIndex + 1).trim();

                // Quitar comillas si están presentes ("..." o '...')
                if ((value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) ||
                    (value.startsWith("'") && value.endsWith("'") && value.length() >= 2)) {
                    value = value.substring(1, value.length() - 1);
                }

                props.put(key, value);
                // Si la variable no fue fijada en el SO ni como -Dproperty, se setea en System
                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                }
            }
            log.info("Cargadas {} variables de entorno desde {}", props.size(), envFile.getAbsolutePath());
        } catch (IOException e) {
            log.warn("No se pudo leer el archivo .env: {}", e.getMessage());
        }
        return props;
    }

    private static File findEnvFile() {
        File current = new File(".env");
        if (current.exists() && current.isFile()) {
            return current;
        }
        File parent = new File("../.env");
        if (parent.exists() && parent.isFile()) {
            return parent;
        }
        return null;
    }
}
