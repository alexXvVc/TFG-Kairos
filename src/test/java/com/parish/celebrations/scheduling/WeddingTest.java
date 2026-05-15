package com.parish.celebrations.scheduling;

import com.parish.celebrations.scheduling.domain.CelebrationStatus;
import com.parish.celebrations.scheduling.domain.Wedding;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WeddingTest {

    @Test
    void cannotConfirmWeddingWithoutDocumentation() {
        Wedding wedding = sampleWedding();
        assertThrows(IllegalStateException.class, wedding::confirm);
    }

    @Test
    void confirmsAfterDocumentationIsComplete() {
        Wedding wedding = sampleWedding();
        wedding.markDocumentationComplete();
        wedding.confirm();
        assertEquals(CelebrationStatus.CONFIRMED, wedding.getStatus());
    }

    private Wedding sampleWedding() {
        return new Wedding(
            UUID.randomUUID(),
            UUID.randomUUID(),
            LocalDateTime.now().plusDays(30),
            UUID.randomUUID(),
            UUID.randomUUID(),
            List.of(UUID.randomUUID(), UUID.randomUUID())
        );
    }
}
