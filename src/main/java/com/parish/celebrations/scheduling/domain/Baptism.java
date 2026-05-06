package com.parish.celebrations.scheduling.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Baptism extends Celebration {

    private final UUID childId;
    private final List<UUID> parentIds;
    private final List<UUID> godparentIds;

    public Baptism(UUID id, LocalDateTime scheduledAt, UUID locationId, UUID presidingPriestId,
                   UUID childId, List<UUID> parentIds, List<UUID> godparentIds) {
        super(id, scheduledAt, locationId, presidingPriestId);

        if (childId == null) {
            throw new IllegalArgumentException("A baptism requires a child");
        }
        if (godparentIds == null || godparentIds.isEmpty()) {
            throw new IllegalArgumentException("A baptism requires at least one godparent");
        }

        this.childId = childId;
        this.parentIds = List.copyOf(parentIds == null ? List.of() : parentIds);
        this.godparentIds = List.copyOf(godparentIds);
    }

    @Override
    public CelebrationType type() {
        return CelebrationType.BAPTISM;
    }

    @Override
    protected void validateForConfirmation() {
        // Confirmation rules can grow here as you learn the domain better.
    }

    public UUID childId() { return childId; }
    public List<UUID> parentIds() { return parentIds; }
    public List<UUID> godparentIds() { return godparentIds; }
}
