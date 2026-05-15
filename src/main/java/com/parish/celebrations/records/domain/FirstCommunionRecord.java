package com.parish.celebrations.records.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@DiscriminatorValue("FIRST_COMMUNION")
public class FirstCommunionRecord extends SacramentalRecord {

    protected FirstCommunionRecord() {}

    public FirstCommunionRecord(UUID celebrationId) {
        super(celebrationId);
    }
}
