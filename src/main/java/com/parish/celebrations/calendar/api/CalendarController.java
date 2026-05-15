package com.parish.celebrations.calendar.api;

import com.parish.celebrations.calendar.api.dto.CalendarEntry;
import com.parish.celebrations.calendar.application.CalendarService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final CalendarService svc;

    public CalendarController(CalendarService svc) { this.svc = svc; }

    @GetMapping
    public List<CalendarEntry> findEntries(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return svc.findEntries(from, to);
    }
}
