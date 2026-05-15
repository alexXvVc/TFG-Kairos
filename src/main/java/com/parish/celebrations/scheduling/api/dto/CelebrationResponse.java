package com.parish.celebrations.scheduling.api.dto;

import com.parish.celebrations.scheduling.domain.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CelebrationResponse(
        UUID id,
        String type,
        String status,
        LocalDateTime scheduledAt,
        UUID locationId,
        UUID presidingPriestId,
        String notes,
        // Mass
        String intention,
        Boolean sundayMass,
        // Baptism
        UUID childId,
        List<UUID> parentIds,
        List<UUID> godparentIds,
        // Wedding
        UUID spouseAId,
        UUID spouseBId,
        List<UUID> witnessIds,
        Boolean documentationComplete,
        // Funeral
        UUID deceasedId,
        String burialLocation,
        // Confirmation
        UUID bishopId,
        List<UUID> candidateIds,
        // FirstCommunion
        Boolean catechismCompleted
) {
    public static CelebrationResponse from(Celebration c) {
        String intention = null; Boolean sundayMass = null;
        UUID childId = null; List<UUID> parentIds = null; List<UUID> godparentIds = null;
        UUID spouseAId = null; UUID spouseBId = null; List<UUID> witnessIds = null; Boolean docComplete = null;
        UUID deceasedId = null; String burialLocation = null;
        UUID bishopId = null; List<UUID> candidateIds = null;
        Boolean catechism = null;

        if (c instanceof Mass m) {
            intention = m.getIntention(); sundayMass = m.isSundayMass();
        } else if (c instanceof Baptism b) {
            childId = b.getChildId(); parentIds = b.getParentIds(); godparentIds = b.getGodparentIds();
        } else if (c instanceof Wedding w) {
            spouseAId = w.getSpouseAId(); spouseBId = w.getSpouseBId();
            witnessIds = w.getWitnessIds(); docComplete = w.isDocumentationComplete();
        } else if (c instanceof Funeral f) {
            deceasedId = f.getDeceasedId(); burialLocation = f.getBurialLocation();
        } else if (c instanceof Confirmation conf) {
            bishopId = conf.getBishopId(); candidateIds = conf.getCandidateIds();
        } else if (c instanceof FirstCommunion fc) {
            catechism = fc.isCatechismCompleted(); candidateIds = fc.getCandidateIds();
        }

        return new CelebrationResponse(
                c.getId(), c.getType() != null ? c.getType().name() : null, c.getStatus().name(),
                c.getScheduledAt(), c.getLocationId(), c.getPresidingPriestId(), c.getNotes(),
                intention, sundayMass, childId, parentIds, godparentIds,
                spouseAId, spouseBId, witnessIds, docComplete,
                deceasedId, burialLocation, bishopId, candidateIds, catechism
        );
    }
}
