package com.parish.celebrations.records.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@DiscriminatorValue("BAPTISM")
public class BaptismRecord extends SacramentalRecord {

    @Column(name = "child_id", columnDefinition = "BINARY(16)")
    private UUID childId;

    @Column(name = "ministering_priest_id", columnDefinition = "BINARY(16)")
    private UUID ministeringPriestId;

    protected BaptismRecord() {}

    public BaptismRecord(UUID celebrationId, UUID childId, UUID ministeringPriestId) {
        super(celebrationId);
        this.childId = childId;
        this.ministeringPriestId = ministeringPriestId;
    }

    public UUID getChildId() { return childId; }
    public UUID getMinisteringPriestId() { return ministeringPriestId; }
}
