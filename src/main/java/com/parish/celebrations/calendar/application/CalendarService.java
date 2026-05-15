package com.parish.celebrations.calendar.application;

import com.parish.celebrations.calendar.api.dto.CalendarEntry;
import com.parish.celebrations.location.domain.Location;
import com.parish.celebrations.location.domain.LocationRepository;
import com.parish.celebrations.person.domain.Person;
import com.parish.celebrations.person.domain.PersonRepository;
import com.parish.celebrations.scheduling.domain.Celebration;
import com.parish.celebrations.scheduling.domain.CelebrationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CalendarService {

    private final CelebrationRepository celebrationRepo;
    private final PersonRepository personRepo;
    private final LocationRepository locationRepo;

    public CalendarService(CelebrationRepository celebrationRepo,
                           PersonRepository personRepo,
                           LocationRepository locationRepo) {
        this.celebrationRepo = celebrationRepo;
        this.personRepo = personRepo;
        this.locationRepo = locationRepo;
    }

    public List<CalendarEntry> findEntries(LocalDateTime from, LocalDateTime to) {
        List<Celebration> celebrations = celebrationRepo.findByScheduledAtBetween(from, to);

        // Collect all UUIDs to resolve
        List<UUID> priestIds = celebrations.stream()
                .map(Celebration::getPresidingPriestId).filter(id -> id != null).distinct().toList();
        List<UUID> locationIds = celebrations.stream()
                .map(Celebration::getLocationId).filter(id -> id != null).distinct().toList();

        Map<UUID, String> priestNames = personRepo.findAllById(priestIds).stream()
                .collect(Collectors.toMap(Person::getId, p -> p.getFirstName() + " " + p.getLastName()));
        Map<UUID, String> locationNames = locationRepo.findAllById(locationIds).stream()
                .collect(Collectors.toMap(Location::getId, Location::getName));

        return celebrations.stream().map(c -> new CalendarEntry(
                c.getId(),
                c.getType() != null ? c.getType().name() : null,
                c.getScheduledAt(),
                c.getLocationId(),
                c.getLocationId() != null ? locationNames.getOrDefault(c.getLocationId(), "") : "",
                c.getPresidingPriestId(),
                c.getPresidingPriestId() != null ? priestNames.getOrDefault(c.getPresidingPriestId(), "") : ""
        )).toList();
    }
}
