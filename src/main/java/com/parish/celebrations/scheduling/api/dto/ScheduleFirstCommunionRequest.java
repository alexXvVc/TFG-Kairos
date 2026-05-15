package com.parish.celebrations.scheduling.api.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ScheduleFirstCommunionRequest(
        LocalDateTime scheduledAt, UUID locationId, UUID presidingPriestId,
        List<UUID> candidateIds) {}
