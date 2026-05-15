package com.parish.celebrations.location.api.dto;

import com.parish.celebrations.location.domain.Location;
import java.util.UUID;

public record LocationResponse(UUID id, String name, String address, Integer capacity, boolean active) {
    public static LocationResponse from(Location l) {
        return new LocationResponse(l.getId(), l.getName(), l.getAddress(), l.getCapacity(), l.isActive());
    }
}
