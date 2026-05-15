package com.parish.celebrations.scheduling.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@DiscriminatorValue("WEDDING")
public class Wedding extends Celebration {

    @Column(name = "spouse_a_id", columnDefinition = "BINARY(16)")
    private UUID spouseAId;

    @Column(name = "spouse_b_id", columnDefinition = "BINARY(16)")
    private UUID spouseBId;

    @Column(name = "documentation_complete")
    private boolean documentationComplete;

    @ElementCollection
    @CollectionTable(name = "wedding_witnesses", joinColumns = @JoinColumn(name = "celebration_id"))
    @Column(name = "person_id", columnDefinition = "BINARY(16)")
    private List<UUID> witnessIds = new ArrayList<>();

    protected Wedding() {}

    public Wedding(UUID locationId, UUID presidingPriestId, LocalDateTime scheduledAt,
                   UUID spouseAId, UUID spouseBId, List<UUID> witnessIds) {
        super(locationId, presidingPriestId, scheduledAt);
        this.spouseAId = spouseAId;
        this.spouseBId = spouseBId;
        this.witnessIds = witnessIds != null ? new ArrayList<>(witnessIds) : new ArrayList<>();
        this.documentationComplete = false;
    }

    public void markDocumentationComplete() { this.documentationComplete = true; }

    public UUID getSpouseAId() { return spouseAId; }
    public UUID getSpouseBId() { return spouseBId; }
    public List<UUID> getWitnessIds() { return witnessIds; }
    public boolean isDocumentationComplete() { return documentationComplete; }
}
