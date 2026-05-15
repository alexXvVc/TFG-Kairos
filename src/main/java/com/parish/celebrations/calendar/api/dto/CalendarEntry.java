package com.parish.celebrations.calendar.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CalendarEntry(
        UUID celebrationId,
        String type,
        LocalDateTime scheduledAt,
        UUID locationId,
        String locationName,
        UUID presidingPriestId,
        String presidingPriestName
) {}
