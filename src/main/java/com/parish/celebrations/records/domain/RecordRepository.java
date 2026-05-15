package com.parish.celebrations.records.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface RecordRepository extends JpaRepository<SacramentalRecord, UUID> {

    List<SacramentalRecord> findByCelebrationId(UUID celebrationId);

    @org.springframework.data.jpa.repository.Query("SELECT r FROM SacramentalRecord r WHERE TYPE(r) = :type")
    Page<SacramentalRecord> findByType(@org.springframework.data.repository.query.Param("type") Class<?> type, Pageable pageable);
}
