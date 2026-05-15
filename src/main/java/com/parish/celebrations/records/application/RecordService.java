package com.parish.celebrations.records.application;

import com.parish.celebrations.records.domain.BaptismRecord;
import com.parish.celebrations.records.domain.ConfirmationRecord;
import com.parish.celebrations.records.domain.FirstCommunionRecord;
import com.parish.celebrations.records.domain.FuneralRecord;
import com.parish.celebrations.records.domain.MarriageRecord;
import com.parish.celebrations.records.domain.RecordRepository;
import com.parish.celebrations.records.domain.RecordType;
import com.parish.celebrations.records.domain.SacramentalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class RecordService {

    private final RecordRepository repo;

    public RecordService(RecordRepository repo) { this.repo = repo; }

    public SacramentalRecord findById(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
    }

    public List<SacramentalRecord> findByCelebration(UUID celebrationId) {
        return repo.findByCelebrationId(celebrationId);
    }

    public Page<SacramentalRecord> findByType(String type, Pageable pageable) {
        Class<?> clazz = switch (RecordType.valueOf(type)) {
            case BAPTISM -> BaptismRecord.class;
            case MARRIAGE -> MarriageRecord.class;
            case FUNERAL -> FuneralRecord.class;
            case CONFIRMATION -> ConfirmationRecord.class;
            case FIRST_COMMUNION -> FirstCommunionRecord.class;
        };
        return repo.findByType(clazz, pageable);
    }

    public Page<SacramentalRecord> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }
}
