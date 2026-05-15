package com.parish.celebrations.scheduling.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleMassRequest(
        LocalDateTime scheduledAt, UUID locationId, UUID presidingPriestId,
        String intention, boolean sundayMass) {}
