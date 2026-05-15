package com.parish.celebrations.scheduling.application;

import com.parish.celebrations.scheduling.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CelebrationService {

    private final CelebrationRepository repo;

    public CelebrationService(CelebrationRepository repo) { this.repo = repo; }

    public Celebration findById(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Celebration not found"));
    }

    public Page<Celebration> findByRange(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return repo.findByScheduledAtBetween(from, to, pageable);
    }

    public Page<Celebration> findByPriest(UUID priestId, Pageable pageable) {
        return repo.findByPresidingPriestId(priestId, pageable);
    }

    public Mass scheduleMass(UUID locationId, UUID priestId, LocalDateTime scheduledAt,
                             String intention, boolean sundayMass) {
        Mass m = new Mass(locationId, priestId, scheduledAt, intention, sundayMass);
        return repo.save(m);
    }

    public Baptism scheduleBaptism(UUID locationId, UUID priestId, LocalDateTime scheduledAt,
                                   UUID childId, List<UUID> parentIds, List<UUID> godparentIds) {
        Baptism b = new Baptism(locationId, priestId, scheduledAt, childId, parentIds, godparentIds);
        return repo.save(b);
    }

    public Wedding scheduleWedding(UUID locationId, UUID priestId, LocalDateTime scheduledAt,
                                   UUID spouseAId, UUID spouseBId, List<UUID> witnessIds) {
        Wedding w = new Wedding(locationId, priestId, scheduledAt, spouseAId, spouseBId, witnessIds);
        return repo.save(w);
    }

    public Funeral scheduleFuneral(UUID locationId, UUID priestId, LocalDateTime scheduledAt,
                                   UUID deceasedId, String burialLocation) {
        Funeral f = new Funeral(locationId, priestId, scheduledAt, deceasedId, burialLocation);
        return repo.save(f);
    }

    public Confirmation scheduleConfirmation(UUID locationId, UUID priestId, LocalDateTime scheduledAt,
                                             List<UUID> candidateIds, UUID bishopId) {
        Confirmation c = new Confirmation(locationId, priestId, scheduledAt, candidateIds, bishopId);
        return repo.save(c);
    }

    public FirstCommunion scheduleFirstCommunion(UUID locationId, UUID priestId, LocalDateTime scheduledAt,
                                                 List<UUID> candidateIds) {
        FirstCommunion fc = new FirstCommunion(locationId, priestId, scheduledAt, candidateIds);
        return repo.save(fc);
    }

    public void confirm(UUID id) {
        Celebration c = findById(id);
        c.confirm();
        repo.save(c);
    }

    public void complete(UUID id) {
        Celebration c = findById(id);
        c.complete();
        repo.save(c);
    }

    public void cancel(UUID id, String reason) {
        Celebration c = findById(id);
        c.cancel(reason);
        repo.save(c);
    }

    public void reschedule(UUID id, LocalDateTime newDateTime) {
        Celebration c = findById(id);
        c.reschedule(newDateTime);
        repo.save(c);
    }

    public void markDocumentationComplete(UUID id) {
        Celebration c = findById(id);
        if (!(c instanceof Wedding w)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a wedding");
        w.markDocumentationComplete();
        repo.save(w);
    }

    public void assignBishop(UUID id, UUID bishopId) {
        Celebration c = findById(id);
        if (!(c instanceof Confirmation conf)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a confirmation");
        conf.assignBishop(bishopId);
        repo.save(conf);
    }

    public void markCatechismComplete(UUID id) {
        Celebration c = findById(id);
        if (!(c instanceof FirstCommunion fc)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not a first communion");
        fc.markCatechismComplete();
        repo.save(fc);
    }
}
