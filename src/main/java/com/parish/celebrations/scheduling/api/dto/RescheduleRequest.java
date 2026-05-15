package com.parish.celebrations.scheduling.api.dto;

import java.time.LocalDateTime;

public record RescheduleRequest(LocalDateTime newDateTime) {}
