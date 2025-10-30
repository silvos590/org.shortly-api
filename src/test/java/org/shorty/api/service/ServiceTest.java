package org.shorty.api.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.shorty.api.database.CouchbaseDatabaseInitializer;
import org.shorty.api.TestUtils;

import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.core.error.context.ErrorContext;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
public class ServiceTest {

    @InjectMock
    CouchbaseDatabaseInitializer couchbaseDatabaseInitializer;


    @BeforeAll
    void setUp() {
        // Mock getBucket()
        when(couchbaseDatabaseInitializer.getBucket()).thenReturn(mock(com.couchbase.client.java.Bucket.class));
        // Mock defaultCollection()
        when(couchbaseDatabaseInitializer.getBucket().defaultCollection()).thenReturn(mock(com.couchbase.client.java.Collection.class));
    }

    @Test
    public void testCreateShortUrl() {
        // Get input/output body JSON from file
        String createShortUrlInput = TestUtils.readResourceAsString("/create-short-url-input.json");
        // String createShortUrlOutput = TestUtils.readResourceAsString("/create-short-url-output.json");
        // String retrieveShortUrlOutput = TestUtils.readResourceAsString("/retrieve-short-url-output.json");
        System.out.println("document: " + TestUtils.readResourceAsString("/document.json"));
        JsonObject document = JsonObject.fromJson(TestUtils.readResourceAsString("/document.json"));


        // Test POST /shorten

        // Mock Couchbase upsert
        when(couchbaseDatabaseInitializer.getBucket().defaultCollection()
                .upsert(any(), any())).thenReturn(null);

        given()
            .contentType("application/json")
            .body(createShortUrlInput)
        .when()
            .post("/shorten")
        .then()
            .statusCode(200)
            //.body(is(createShortUrlOutput));
            .body("originalUrl", is("https://example.com/very/long/path?param=value"));

        // // Verify the mock was used
        // verify(couchbaseDatabaseInitializer.getBucket().defaultCollection()).upsert("abc123", createShortUrlInput);

        // Test GET /short/{shortCode}
        GetResult mockResult = mock(GetResult.class);
        when(mockResult.contentAsObject()).thenReturn(document);

        when(couchbaseDatabaseInitializer.getBucket().defaultCollection()
                .get("abc123")).thenReturn(mockResult);

        given()
        .when()
            .get("/shorten/abc123")
        .then()
            .statusCode(200)
            .body("shortCode", is("abc123"))
            .body("originalUrl", is("https://example.com/very/long/path?param=value"))
            .body("isActive", is(true))
            .body("clickCount", is(0))
            .body("id", is("769a2439-5757-4e00-aaa6-5a85b15b12a8"))
            .body("createdAt", is("2025-10-26T13:56:50.2197914Z"));

        // Test that short code is not upsert twice
        when(couchbaseDatabaseInitializer.getBucket().defaultCollection()
                .upsert(any(), any())).thenThrow( DocumentExistsException.class);

        given()
            .contentType("application/json")
            .body(createShortUrlInput)
        .when()
            .post("/shorten")
        .then()
            .statusCode(400);
    }

}
