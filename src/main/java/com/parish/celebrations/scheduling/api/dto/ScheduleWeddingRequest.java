package com.parish.celebrations.scheduling.api.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ScheduleWeddingRequest(
        LocalDateTime scheduledAt, UUID locationId, UUID presidingPriestId,
        UUID spouseAId, UUID spouseBId, List<UUID> witnessIds) {}
