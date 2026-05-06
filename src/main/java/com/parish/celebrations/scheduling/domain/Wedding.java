package com.parish.celebrations.scheduling.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Demonstrates the value of putting validation in the domain.
 *
 * The frontend can be as simple as a form. The domain rejects
 * impossible weddings — and the error message comes from here,
 * not scattered across controllers.
 */
public class Wedding extends Celebration {

    private final UUID spouseAId;
    private final UUID spouseBId;
    private final List<UUID> witnessIds;
    private boolean documentationComplete;

    public Wedding(UUID id, LocalDateTime scheduledAt, UUID locationId, UUID presidingPriestId,
                   UUID spouseAId, UUID spouseBId, List<UUID> witnessIds) {
        super(id, scheduledAt, locationId, presidingPriestId);

        if (spouseAId == null || spouseBId == null) {
            throw new IllegalArgumentException("A wedding requires two spouses");
        }
        if (spouseAId.equals(spouseBId)) {
            throw new IllegalArgumentException("Spouses must be two different people");
        }
        if (witnessIds == null || witnessIds.size() < 2) {
            throw new IllegalArgumentException("A wedding requires at least two witnesses");
        }

        this.spouseAId = spouseAId;
        this.spouseBId = spouseBId;
        this.witnessIds = List.copyOf(witnessIds);
        this.documentationComplete = false;
    }

    public void markDocumentationComplete() {
        this.documentationComplete = true;
    }

    @Override
    public CelebrationType type() {
        return CelebrationType.WEDDING;
    }

    @Override
    protected void validateForConfirmation() {
        if (!documentationComplete) {
            throw new IllegalStateException(
                "Wedding cannot be confirmed: civil and parish documentation must be complete");
        }
    }

    public UUID spouseAId() { return spouseAId; }
    public UUID spouseBId() { return spouseBId; }
    public List<UUID> witnessIds() { return witnessIds; }
    public boolean isDocumentationComplete() { return documentationComplete; }
}
