package com.parish.celebrations.scheduling.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "celebration")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class Celebration {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CelebrationStatus status;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(name = "location_id", columnDefinition = "BINARY(16)")
    private UUID locationId;

    @Column(name = "presiding_priest_id", columnDefinition = "BINARY(16)")
    private UUID presidingPriestId;

    private String notes;

    protected Celebration() {}

    protected Celebration(UUID locationId, UUID presidingPriestId, LocalDateTime scheduledAt) {
        this.id = UUID.randomUUID();
        this.status = CelebrationStatus.DRAFT;
        this.locationId = locationId;
        this.presidingPriestId = presidingPriestId;
        this.scheduledAt = scheduledAt;
    }

    public void confirm() {
        if (status != CelebrationStatus.DRAFT) throw new IllegalStateException("Only DRAFT celebrations can be confirmed");
        this.status = CelebrationStatus.CONFIRMED;
    }

    public void complete() {
        if (status != CelebrationStatus.CONFIRMED) throw new IllegalStateException("Only CONFIRMED celebrations can be completed");
        this.status = CelebrationStatus.COMPLETED;
    }

    public void cancel(String reason) {
        if (status == CelebrationStatus.COMPLETED) throw new IllegalStateException("Cannot cancel a completed celebration");
        this.status = CelebrationStatus.CANCELLED;
        this.notes = (notes == null ? "" : notes + "\n") + "Cancelled: " + reason;
    }

    public void reschedule(LocalDateTime newDateTime) {
        if (status == CelebrationStatus.COMPLETED || status == CelebrationStatus.CANCELLED)
            throw new IllegalStateException("Cannot reschedule in status " + status);
        this.scheduledAt = newDateTime;
    }

    public UUID getId() { return id; }
    public CelebrationType getType() {
        if (this instanceof Mass) return CelebrationType.MASS;
        if (this instanceof Baptism) return CelebrationType.BAPTISM;
        if (this instanceof Wedding) return CelebrationType.WEDDING;
        if (this instanceof Funeral) return CelebrationType.FUNERAL;
        if (this instanceof Confirmation) return CelebrationType.CONFIRMATION;
        if (this instanceof FirstCommunion) return CelebrationType.FIRST_COMMUNION;
        return null;
    }
    public CelebrationStatus getStatus() { return status; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public UUID getLocationId() { return locationId; }
    public UUID getPresidingPriestId() { return presidingPriestId; }
    public String getNotes() { return notes; }
}
