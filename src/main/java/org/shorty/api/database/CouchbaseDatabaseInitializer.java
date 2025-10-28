package org.shorty.api.database;

import java.time.Duration;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;

import io.quarkus.runtime.Startup;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Startup
public class CouchbaseDatabaseInitializer {

    private static final Logger logger = LoggerFactory.getLogger(CouchbaseDatabaseInitializer.class);

    @Inject
    private Cluster cluster;

    private Bucket bucket;

    void init(@Observes StartupEvent event) {
        cluster.bucket("shortly-urls").waitUntilReady(Duration.ofSeconds(30));
        logger.info("Couchbase bucket 'shortly-urls' is ready.");
        this.bucket = cluster.bucket("shortly-urls");
    }

    @PreDestroy
    void cleanup() {
        if (cluster != null) {
            cluster.disconnect();
            logger.info("🧹 Couchbase connection closed.");
        }
    }

    public Bucket getBucket() {
        return bucket;
    }
}
