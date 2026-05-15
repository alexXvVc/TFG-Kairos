package com.parish.celebrations.location.application;

import com.parish.celebrations.location.domain.Location;
import com.parish.celebrations.location.domain.LocationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class LocationService {

    private final LocationRepository repo;

    public LocationService(LocationRepository repo) { this.repo = repo; }

    public List<Location> findAll() { return repo.findAll(); }

    public Location findById(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found"));
    }
}
