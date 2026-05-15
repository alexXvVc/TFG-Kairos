package com.parish.celebrations.scheduling.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@DiscriminatorValue("FUNERAL")
public class Funeral extends Celebration {

    @Column(name = "deceased_id", columnDefinition = "BINARY(16)")
    private UUID deceasedId;

    @Column(name = "burial_location")
    private String burialLocation;

    protected Funeral() {}

    public Funeral(UUID locationId, UUID presidingPriestId, LocalDateTime scheduledAt,
                   UUID deceasedId, String burialLocation) {
        super(locationId, presidingPriestId, scheduledAt);
        this.deceasedId = deceasedId;
        this.burialLocation = burialLocation;
    }

    public UUID getDeceasedId() { return deceasedId; }
    public String getBurialLocation() { return burialLocation; }
}
