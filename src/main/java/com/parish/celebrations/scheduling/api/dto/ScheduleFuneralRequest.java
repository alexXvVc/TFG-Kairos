package com.parish.celebrations.scheduling.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleFuneralRequest(
        LocalDateTime scheduledAt, UUID locationId, UUID presidingPriestId,
        UUID deceasedId, String burialLocation) {}
