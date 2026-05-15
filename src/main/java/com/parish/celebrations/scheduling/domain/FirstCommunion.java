package com.parish.celebrations.scheduling.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@DiscriminatorValue("FIRST_COMMUNION")
public class FirstCommunion extends Celebration {

    @Column(name = "catechism_completed")
    private boolean catechismCompleted;

    @ElementCollection
    @CollectionTable(name = "first_communion_candidates", joinColumns = @JoinColumn(name = "celebration_id"))
    @Column(name = "person_id", columnDefinition = "BINARY(16)")
    private List<UUID> candidateIds = new ArrayList<>();

    protected FirstCommunion() {}

    public FirstCommunion(UUID locationId, UUID presidingPriestId, LocalDateTime scheduledAt,
                          List<UUID> candidateIds) {
        super(locationId, presidingPriestId, scheduledAt);
        this.candidateIds = candidateIds != null ? new ArrayList<>(candidateIds) : new ArrayList<>();
        this.catechismCompleted = false;
    }

    public void markCatechismComplete() { this.catechismCompleted = true; }

    public boolean isCatechismCompleted() { return catechismCompleted; }
    public List<UUID> getCandidateIds() { return candidateIds; }
}
