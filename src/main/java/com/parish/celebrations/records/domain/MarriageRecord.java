package com.parish.celebrations.records.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@DiscriminatorValue("MARRIAGE")
public class MarriageRecord extends SacramentalRecord {

    @Column(name = "spouse_a_id", columnDefinition = "BINARY(16)")
    private UUID spouseAId;

    @Column(name = "spouse_b_id", columnDefinition = "BINARY(16)")
    private UUID spouseBId;

    protected MarriageRecord() {}

    public MarriageRecord(UUID celebrationId, UUID spouseAId, UUID spouseBId) {
        super(celebrationId);
        this.spouseAId = spouseAId;
        this.spouseBId = spouseBId;
    }

    public UUID getSpouseAId() { return spouseAId; }
    public UUID getSpouseBId() { return spouseBId; }
}
