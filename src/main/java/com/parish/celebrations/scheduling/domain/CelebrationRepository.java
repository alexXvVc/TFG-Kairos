package com.parish.celebrations.scheduling.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain-side contract for persisting Celebrations.
 *
 * The implementation (using JPA, MySQL, etc.) lives in the infrastructure
 * package. The domain doesn't know how it's persisted — only that it can be.
 */
public interface CelebrationRepository {

    void save(Celebration celebration);

    Optional<Celebration> findById(UUID id);

    List<Celebration> findBetween(LocalDateTime from, LocalDateTime to);

    List<Celebration> findByPresidingPriest(UUID priestId);
}
