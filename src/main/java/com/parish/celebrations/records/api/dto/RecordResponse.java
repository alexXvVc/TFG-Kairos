package com.parish.celebrations.records.api.dto;

import com.parish.celebrations.records.domain.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record RecordResponse(
        UUID id,
        UUID celebrationId,
        LocalDate registeredOn,
        String recordType,
        String notes,
        UUID childId,
        UUID ministeringPriestId,
        UUID spouseAId,
        UUID spouseBId,
        UUID deceasedId,
        UUID bishopId,
        List<UUID> candidateIds
) {
    public static RecordResponse from(SacramentalRecord r) {
        UUID childId = null, ministeringPriestId = null;
        UUID spouseAId = null, spouseBId = null;
        UUID deceasedId = null;
        UUID bishopId = null;
        List<UUID> candidateIds = null;

        if (r instanceof BaptismRecord b) {
            childId = b.getChildId(); ministeringPriestId = b.getMinisteringPriestId();
        } else if (r instanceof MarriageRecord m) {
            spouseAId = m.getSpouseAId(); spouseBId = m.getSpouseBId();
        } else if (r instanceof FuneralRecord f) {
            deceasedId = f.getDeceasedId();
        } else if (r instanceof ConfirmationRecord c) {
            bishopId = c.getBishopId(); candidateIds = c.getCandidateIds();
        }

        return new RecordResponse(
                r.getId(), r.getCelebrationId(), r.getRegisteredOn(),
                r.getRecordType() != null ? r.getRecordType().name() : null,
                r.getNotes(), childId, ministeringPriestId,
                spouseAId, spouseBId, deceasedId, bishopId, candidateIds
        );
    }
}
