package org.shorty.api.service;

import org.shorty.api.database.CouchbaseDatabaseInitializer;
import org.shorty.api.exception.ServiceException;
import org.shorty.api.exception.BadRequestException;
import org.shorty.api.utils.Utils;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.shorty.api.UrlsApi;
import org.shorty.api.model.CreateShortUrlRequest;
import org.shorty.api.model.ShortUrl;
import org.shorty.api.model.ShortUrlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ShortlyServiceImpl implements UrlsApi {

    private static Logger logger = LoggerFactory.getLogger(ShortlyServiceImpl.class);

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    private CouchbaseDatabaseInitializer couchbaseDatabaseInitializer;

    @ConfigProperty(name = "document.max.expiration.days", defaultValue = "10")
    int documentMaxExpirationDays;

    @Override
    public ShortUrlResponse createShortUrl(CreateShortUrlRequest createShortUrlRequest) {
        logger.info("Creating short URL for original URL: {}", createShortUrlRequest.getOriginalUrl());

        // Create ShortUrl object
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setOriginalUrl(createShortUrlRequest.getOriginalUrl());
        shortUrl.setCustomAlias(createShortUrlRequest.getCustomAlias());
        shortUrl.setTitle(createShortUrlRequest.getTitle());
        shortUrl.setCreatedAt(java.time.OffsetDateTime.now());
        shortUrl.setShortCode(generateShortCode());
        shortUrl.setId(java.util.UUID.randomUUID());
        shortUrl.setDescription(createShortUrlRequest.getDescription());
        shortUrl.setExpiresAt(Utils.computeMaxExpirationDate(createShortUrlRequest.getExpiresAt(), documentMaxExpirationDays));

        // Store in Couchbase
        try {
            logger.info("Connected to Couchbase bucket 'shortly-urls'");

            String json = objectMapper.writeValueAsString(shortUrl);
            JsonObject jsonObject = JsonObject.fromJson(json);

            couchbaseDatabaseInitializer.getBucket().defaultCollection()
                    .upsert(shortUrl.getShortCode(), jsonObject);
        } catch (DocumentExistsException e) {
            throw new BadRequestException("Short code already exists");
        } catch (JsonProcessingException e) {
            throw new ServiceException("Failed to store short URL");
        }

        // return a dummy response for now
        return new ShortUrlResponse()
                .id(shortUrl.getId())
                .shortCode(shortUrl.getShortCode())
                .originalUrl(createShortUrlRequest.getOriginalUrl())
                .createdAt(shortUrl.getCreatedAt())
                .expiresAt(shortUrl.getExpiresAt());
    }

    @Override
    public ShortUrlResponse getShortUrl(String shortCode) {
        logger.info("Retrieving short URL details for short code: {}", shortCode);

        // Retrieve from Couchbase
        GetResult result = null;
        try {
            result = couchbaseDatabaseInitializer.getBucket().defaultCollection()
                    .get(shortCode);
        } catch (Exception e) {
            throw new ServiceException("Failed to retrieve short URL");
        }

        if (result == null) {
            throw new ServiceException("Short URL not found");
        } else {
            logger.info("Short URL found in Couchbase for short code: {}", shortCode);
            ShortUrl shortUrl = null;
            try {
                shortUrl = objectMapper.readValue(result.contentAsObject().toString(), ShortUrl.class);
            } catch (JsonProcessingException e) {
                throw new ServiceException("Failed to retrieve short URL");
            }

            return new ShortUrlResponse()
                    .id(shortUrl.getId())
                    .shortCode(shortCode)
                    .originalUrl(shortUrl.getOriginalUrl())
                    .createdAt(shortUrl.getCreatedAt());
        }
    }

    private String generateShortCode() {
        // generate a random 6-character alphanumeric short code
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder shortCode = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 6; i++) {
            shortCode.append(chars.charAt(random.nextInt(chars.length())));
        }
        return shortCode.toString();
    }

}
