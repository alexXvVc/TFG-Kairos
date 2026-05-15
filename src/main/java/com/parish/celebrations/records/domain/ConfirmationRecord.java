package com.parish.celebrations.records.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@DiscriminatorValue("CONFIRMATION")
public class ConfirmationRecord extends SacramentalRecord {

    @Column(name = "bishop_id", columnDefinition = "BINARY(16)")
    private UUID bishopId;

    @ElementCollection
    @CollectionTable(name = "confirmation_record_candidates", joinColumns = @JoinColumn(name = "record_id"))
    @Column(name = "person_id", columnDefinition = "BINARY(16)")
    private List<UUID> candidateIds = new ArrayList<>();

    protected ConfirmationRecord() {}

    public ConfirmationRecord(UUID celebrationId, UUID bishopId, List<UUID> candidateIds) {
        super(celebrationId);
        this.bishopId = bishopId;
        this.candidateIds = candidateIds != null ? new ArrayList<>(candidateIds) : new ArrayList<>();
    }

    public UUID getBishopId() { return bishopId; }
    public List<UUID> getCandidateIds() { return candidateIds; }
}
