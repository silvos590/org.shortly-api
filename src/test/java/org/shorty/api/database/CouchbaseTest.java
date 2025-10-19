package org.shorty.api.database;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.utility.DockerImageName;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;

import java.time.Duration;

public class CouchbaseTest {

    private static final DockerImageName couchbaseImage = DockerImageName.parse("couchbase/server:community-7.0.2");

    private static CouchbaseContainer container = new CouchbaseContainer(couchbaseImage)
            .withBucket(new BucketDefinition("mybucket"));

    @BeforeAll
    public static void setUp() {
        // Initialize Couchbase cluster
        container.start();
    }

    @AfterAll
    public static void tearDown() {
        // Stop Couchbase container
        container.stop();
    }

    @Test
    public void testCouchbaseConnection() {
        // Test Couchbase connection
        Cluster cluster = Cluster.connect(
            container.getConnectionString(),
            container.getUsername(),
            container.getPassword()
        );

        Bucket bucket = cluster.bucket("mybucket");
        bucket.waitUntilReady(Duration.ofSeconds(5));
    }

}
