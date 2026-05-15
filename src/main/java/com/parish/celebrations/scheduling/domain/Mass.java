package com.parish.celebrations.scheduling.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@DiscriminatorValue("MASS")
public class Mass extends Celebration {

    private String intention;

    @Column(name = "sunday_mass")
    private boolean sundayMass;

    protected Mass() {}

    public Mass(UUID locationId, UUID presidingPriestId, LocalDateTime scheduledAt,
                String intention, boolean sundayMass) {
        super(locationId, presidingPriestId, scheduledAt);
        this.intention = intention;
        this.sundayMass = sundayMass;
    }

    public String getIntention() { return intention; }
    public boolean isSundayMass() { return sundayMass; }
}
