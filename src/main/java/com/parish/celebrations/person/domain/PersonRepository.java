package com.parish.celebrations.person.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {

    Page<Person> findByRole(PersonRole role, Pageable pageable);

    @Query("SELECT p FROM Person p WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(p.lastName) LIKE LOWER(CONCAT('%',:q,'%'))")
    Page<Person> search(@Param("q") String q, Pageable pageable);
}
