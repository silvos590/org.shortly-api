package org.shorty.api.utils;

import java.time.OffsetDateTime;

public class Utils {

    static public OffsetDateTime computeMaxExpirationDate(OffsetDateTime expiresAt, int documentMaxExpirationDays) {
        // If no expiration date is provided, use the default from config
        if (expiresAt == null || expiresAt.isAfter(OffsetDateTime.now().plusDays(documentMaxExpirationDays))) {
            expiresAt = OffsetDateTime.now().plusDays(documentMaxExpirationDays);
        }
        return expiresAt;
    }
}
