package com.parish.celebrations.records.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@DiscriminatorValue("FUNERAL")
public class FuneralRecord extends SacramentalRecord {

    @Column(name = "deceased_id", columnDefinition = "BINARY(16)")
    private UUID deceasedId;

    protected FuneralRecord() {}

    public FuneralRecord(UUID celebrationId, UUID deceasedId) {
        super(celebrationId);
        this.deceasedId = deceasedId;
    }

    public UUID getDeceasedId() { return deceasedId; }
}
