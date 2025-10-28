package org.shorty.api;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestUtils {

    public static String readResourceAsString(String resourcePath) {
        try (InputStream inputStream = TestUtils.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read resource: " + resourcePath, e);
        }
    }
}
