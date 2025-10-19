package org.shorty.api.service;

import org.shorty.api.UrlsApi;
import org.shorty.api.model.CreateShortUrlRequest;
import org.shorty.api.model.ShortUrlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.Cluster;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ShortServiceImpl implements UrlsApi {

    private static Logger logger = LoggerFactory.getLogger(ShortServiceImpl.class);

    @Inject
    private Cluster couchbaseCluster;

    @Override
    public ShortUrlResponse createShortUrl(CreateShortUrlRequest createShortUrlRequest) {
        logger.info("Creating short URL for original URL: {}", createShortUrlRequest.getOriginalUrl());

        // return a dummy response for now
        return new ShortUrlResponse()
                .id(java.util.UUID.randomUUID())
                .shortCode("abc123")
                .originalUrl(createShortUrlRequest.getOriginalUrl())
                .createdAt(java.time.OffsetDateTime.now());
    }

    @Override
    public ShortUrlResponse getShortUrl(String shortCode) {
        logger.info("Retrieving short URL details for short code: {}", shortCode);

        // return a dummy response for now
        return new ShortUrlResponse()
                .id(java.util.UUID.randomUUID())
                .shortCode(shortCode)
                .originalUrl(java.net.URI.create("https://example.com/original-url"))
                .createdAt(java.time.OffsetDateTime.now());
    }
}
