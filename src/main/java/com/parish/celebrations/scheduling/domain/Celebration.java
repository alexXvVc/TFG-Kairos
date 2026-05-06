package com.parish.celebrations.scheduling.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate root for the scheduling context.
 *
 * Subtypes (Mass, Baptism, Wedding...) enforce their own invariants.
 * This base class holds what every celebration shares: when, where, who presides.
 *
 * Persistence note: keep JPA annotations out of this file when possible.
 * If you choose to annotate directly (pragmatic), use JOINED inheritance.
 */
public abstract class Celebration {

    private final UUID id;
    private LocalDateTime scheduledAt;
    private UUID locationId;          // reference to Location aggregate by ID only
    private UUID presidingPriestId;   // reference to Person aggregate by ID only
    private CelebrationStatus status;
    private String notes;

    protected Celebration(UUID id, LocalDateTime scheduledAt, UUID locationId, UUID presidingPriestId) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.scheduledAt = Objects.requireNonNull(scheduledAt, "scheduledAt is required");
        this.locationId = Objects.requireNonNull(locationId, "locationId is required");
        this.presidingPriestId = Objects.requireNonNull(presidingPriestId, "presidingPriestId is required");
        this.status = CelebrationStatus.DRAFT;
    }

    /** Each subtype declares its kind for projections, reports, and policies. */
    public abstract CelebrationType type();

    /**
     * Subtypes implement this to assert that all required participants and data
     * are present before a celebration can be confirmed. Throws if invalid.
     */
    protected abstract void validateForConfirmation();

    public void confirm() {
        if (status != CelebrationStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT celebrations can be confirmed");
        }
        validateForConfirmation();
        this.status = CelebrationStatus.CONFIRMED;
    }

    public void cancel(String reason) {
        if (status == CelebrationStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed celebration");
        }
        this.status = CelebrationStatus.CANCELLED;
        this.notes = (notes == null ? "" : notes + "\n") + "Cancelled: " + reason;
    }

    public void reschedule(LocalDateTime newDateTime) {
        if (status == CelebrationStatus.COMPLETED || status == CelebrationStatus.CANCELLED) {
            throw new IllegalStateException("Cannot reschedule a celebration in status " + status);
        }
        this.scheduledAt = Objects.requireNonNull(newDateTime);
    }

    public UUID id() { return id; }
    public LocalDateTime scheduledAt() { return scheduledAt; }
    public UUID locationId() { return locationId; }
    public UUID presidingPriestId() { return presidingPriestId; }
    public CelebrationStatus status() { return status; }
    public String notes() { return notes; }
}
