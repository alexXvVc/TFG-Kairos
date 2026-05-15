package com.parish.celebrations.records.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "sacramental_record")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "record_type", discriminatorType = DiscriminatorType.STRING)
public abstract class SacramentalRecord {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "celebration_id", columnDefinition = "BINARY(16)")
    private UUID celebrationId;

    @Column(name = "registered_on", nullable = false)
    private LocalDate registeredOn;

    private String notes;

    protected SacramentalRecord() {}

    protected SacramentalRecord(UUID celebrationId) {
        this.id = UUID.randomUUID();
        this.celebrationId = celebrationId;
        this.registeredOn = LocalDate.now();
    }

    public UUID getId() { return id; }
    public RecordType getRecordType() {
        if (this instanceof BaptismRecord) return RecordType.BAPTISM;
        if (this instanceof MarriageRecord) return RecordType.MARRIAGE;
        if (this instanceof FuneralRecord) return RecordType.FUNERAL;
        if (this instanceof ConfirmationRecord) return RecordType.CONFIRMATION;
        if (this instanceof FirstCommunionRecord) return RecordType.FIRST_COMMUNION;
        return null;
    }
    public UUID getCelebrationId() { return celebrationId; }
    public LocalDate getRegisteredOn() { return registeredOn; }
    public String getNotes() { return notes; }
}
