package com.parish.celebrations.scheduling.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@DiscriminatorValue("CONFIRMATION")
public class Confirmation extends Celebration {

    @Column(name = "bishop_id", columnDefinition = "BINARY(16)")
    private UUID bishopId;

    @ElementCollection
    @CollectionTable(name = "confirmation_candidates", joinColumns = @JoinColumn(name = "celebration_id"))
    @Column(name = "person_id", columnDefinition = "BINARY(16)")
    private List<UUID> candidateIds = new ArrayList<>();

    protected Confirmation() {}

    public Confirmation(UUID locationId, UUID presidingPriestId, LocalDateTime scheduledAt,
                        List<UUID> candidateIds, UUID bishopId) {
        super(locationId, presidingPriestId, scheduledAt);
        this.candidateIds = candidateIds != null ? new ArrayList<>(candidateIds) : new ArrayList<>();
        this.bishopId = bishopId;
    }

    public void assignBishop(UUID bishopId) { this.bishopId = bishopId; }

    public UUID getBishopId() { return bishopId; }
    public List<UUID> getCandidateIds() { return candidateIds; }
}
