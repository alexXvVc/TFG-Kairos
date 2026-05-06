package com.parish.celebrations.scheduling.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Mass extends Celebration {

    private String intention;       // e.g. "For the soul of Maria Garcia"
    private boolean sundayMass;

    public Mass(UUID id, LocalDateTime scheduledAt, UUID locationId, UUID presidingPriestId,
                String intention, boolean sundayMass) {
        super(id, scheduledAt, locationId, presidingPriestId);
        this.intention = intention;
        this.sundayMass = sundayMass;
    }

    @Override
    public CelebrationType type() {
        return CelebrationType.MASS;
    }

    @Override
    protected void validateForConfirmation() {
        // A regular Mass has no extra invariants beyond the base ones.
    }

    public String intention() { return intention; }
    public boolean isSundayMass() { return sundayMass; }
}
