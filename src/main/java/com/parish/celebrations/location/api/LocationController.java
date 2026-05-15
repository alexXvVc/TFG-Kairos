package com.parish.celebrations.location.api;

import com.parish.celebrations.location.api.dto.LocationResponse;
import com.parish.celebrations.location.application.LocationService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService svc;

    public LocationController(LocationService svc) { this.svc = svc; }

    @GetMapping
    public List<LocationResponse> findAll() {
        return svc.findAll().stream().map(LocationResponse::from).toList();
    }
}
