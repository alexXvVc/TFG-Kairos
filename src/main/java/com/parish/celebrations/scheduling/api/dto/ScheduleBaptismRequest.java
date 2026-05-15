package com.parish.celebrations.scheduling.api.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ScheduleBaptismRequest(
        LocalDateTime scheduledAt, UUID locationId, UUID presidingPriestId,
        UUID childId, List<UUID> parentIds, List<UUID> godparentIds) {}
