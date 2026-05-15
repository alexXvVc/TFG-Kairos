package com.parish.celebrations.scheduling.api;

import com.parish.celebrations.scheduling.api.dto.*;
import com.parish.celebrations.scheduling.application.CelebrationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/celebrations")
public class CelebrationController {

    private final CelebrationService svc;

    public CelebrationController(CelebrationService svc) { this.svc = svc; }

    @GetMapping
    public Page<CelebrationResponse> findAll(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("scheduledAt"));
        return svc.findByRange(from, to, pageable).map(CelebrationResponse::from);
    }

    @GetMapping("/{id}")
    public CelebrationResponse findById(@PathVariable UUID id) {
        return CelebrationResponse.from(svc.findById(id));
    }

    @GetMapping("/by-priest/{priestId}")
    public Page<CelebrationResponse> findByPriest(
            @PathVariable UUID priestId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return svc.findByPriest(priestId, PageRequest.of(page, size)).map(CelebrationResponse::from);
    }

    @PostMapping("/masses")
    @ResponseStatus(HttpStatus.CREATED)
    public CelebrationResponse scheduleMass(@RequestBody ScheduleMassRequest req) {
        return CelebrationResponse.from(
                svc.scheduleMass(req.locationId(), req.presidingPriestId(), req.scheduledAt(),
                        req.intention(), req.sundayMass()));
    }

    @PostMapping("/baptisms")
    @ResponseStatus(HttpStatus.CREATED)
    public CelebrationResponse scheduleBaptism(@RequestBody ScheduleBaptismRequest req) {
        return CelebrationResponse.from(
                svc.scheduleBaptism(req.locationId(), req.presidingPriestId(), req.scheduledAt(),
                        req.childId(), req.parentIds(), req.godparentIds()));
    }

    @PostMapping("/weddings")
    @ResponseStatus(HttpStatus.CREATED)
    public CelebrationResponse scheduleWedding(@RequestBody ScheduleWeddingRequest req) {
        return CelebrationResponse.from(
                svc.scheduleWedding(req.locationId(), req.presidingPriestId(), req.scheduledAt(),
                        req.spouseAId(), req.spouseBId(), req.witnessIds()));
    }

    @PostMapping("/funerals")
    @ResponseStatus(HttpStatus.CREATED)
    public CelebrationResponse scheduleFuneral(@RequestBody ScheduleFuneralRequest req) {
        return CelebrationResponse.from(
                svc.scheduleFuneral(req.locationId(), req.presidingPriestId(), req.scheduledAt(),
                        req.deceasedId(), req.burialLocation()));
    }

    @PostMapping("/confirmations")
    @ResponseStatus(HttpStatus.CREATED)
    public CelebrationResponse scheduleConfirmation(@RequestBody ScheduleConfirmationRequest req) {
        return CelebrationResponse.from(
                svc.scheduleConfirmation(req.locationId(), req.presidingPriestId(), req.scheduledAt(),
                        req.candidateIds(), req.bishopId()));
    }

    @PostMapping("/first-communions")
    @ResponseStatus(HttpStatus.CREATED)
    public CelebrationResponse scheduleFirstCommunion(@RequestBody ScheduleFirstCommunionRequest req) {
        return CelebrationResponse.from(
                svc.scheduleFirstCommunion(req.locationId(), req.presidingPriestId(), req.scheduledAt(),
                        req.candidateIds()));
    }

    @PostMapping("/{id}/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirm(@PathVariable UUID id) { svc.confirm(id); }

    @PostMapping("/{id}/complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void complete(@PathVariable UUID id) { svc.complete(id); }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable UUID id, @RequestBody CancelRequest req) { svc.cancel(id, req.reason()); }

    @PostMapping("/{id}/reschedule")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reschedule(@PathVariable UUID id, @RequestBody RescheduleRequest req) {
        svc.reschedule(id, req.newDateTime());
    }

    @PostMapping("/{id}/mark-documentation-complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markDocumentationComplete(@PathVariable UUID id) { svc.markDocumentationComplete(id); }

    @PostMapping("/{id}/assign-bishop")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignBishop(@PathVariable UUID id, @RequestBody Map<String, UUID> body) {
        svc.assignBishop(id, body.get("bishopId"));
    }

    @PostMapping("/{id}/mark-catechism-complete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markCatechismComplete(@PathVariable UUID id) { svc.markCatechismComplete(id); }
}
