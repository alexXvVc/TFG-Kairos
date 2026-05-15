package com.parish.celebrations.scheduling.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@DiscriminatorValue("BAPTISM")
public class Baptism extends Celebration {

    @Column(name = "child_id", columnDefinition = "BINARY(16)")
    private UUID childId;

    @ElementCollection
    @CollectionTable(name = "baptism_parents", joinColumns = @JoinColumn(name = "celebration_id"))
    @Column(name = "person_id", columnDefinition = "BINARY(16)")
    private List<UUID> parentIds = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "baptism_godparents", joinColumns = @JoinColumn(name = "celebration_id"))
    @Column(name = "person_id", columnDefinition = "BINARY(16)")
    private List<UUID> godparentIds = new ArrayList<>();

    protected Baptism() {}

    public Baptism(UUID locationId, UUID presidingPriestId, LocalDateTime scheduledAt,
                   UUID childId, List<UUID> parentIds, List<UUID> godparentIds) {
        super(locationId, presidingPriestId, scheduledAt);
        this.childId = childId;
        this.parentIds = parentIds != null ? new ArrayList<>(parentIds) : new ArrayList<>();
        this.godparentIds = godparentIds != null ? new ArrayList<>(godparentIds) : new ArrayList<>();
    }

    public UUID getChildId() { return childId; }
    public List<UUID> getParentIds() { return parentIds; }
    public List<UUID> getGodparentIds() { return godparentIds; }
}
