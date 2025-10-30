package org.shorty.api.utils;

import io.quarkus.test.junit.QuarkusTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UtilsTest {

    @Test
    public void testComputeMaxExpirationDate() {
        int maxDays = 10;

        // Test case 1: expiresAt is null
        OffsetDateTime result1 = Utils.computeMaxExpirationDate(null, maxDays);
        assertTrue(result1.isBefore(OffsetDateTime.now().plusDays(maxDays + 1)));

        // Test case 2: expiresAt is within max days
        OffsetDateTime input2 = OffsetDateTime.now().plusDays(5);
        OffsetDateTime result2 = Utils.computeMaxExpirationDate(input2, maxDays);
        assertEquals(input2, result2);

        // Test case 3: expiresAt exceeds max days
        OffsetDateTime input3 = OffsetDateTime.now().plusDays(15);
        OffsetDateTime result3 = Utils.computeMaxExpirationDate(input3, maxDays);
        assertTrue(result3.isBefore(OffsetDateTime.now().plusDays(maxDays + 1)));
    }

}
