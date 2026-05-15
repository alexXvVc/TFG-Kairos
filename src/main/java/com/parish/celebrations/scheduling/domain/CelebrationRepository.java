package com.parish.celebrations.scheduling.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CelebrationRepository extends JpaRepository<Celebration, UUID> {

    Page<Celebration> findByScheduledAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    List<Celebration> findByScheduledAtBetween(LocalDateTime from, LocalDateTime to);

    Page<Celebration> findByPresidingPriestId(UUID priestId, Pageable pageable);
}
