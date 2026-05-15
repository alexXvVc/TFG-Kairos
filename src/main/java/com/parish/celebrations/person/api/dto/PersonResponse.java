package com.parish.celebrations.person.api.dto;

import com.parish.celebrations.person.domain.Person;
import java.time.LocalDate;
import java.util.UUID;

public record PersonResponse(
        UUID id,
        String firstName,
        String lastName,
        String role,
        LocalDate dateOfBirth,
        String email,
        String phone,
        String address
) {
    public static PersonResponse from(Person p) {
        return new PersonResponse(
                p.getId(), p.getFirstName(), p.getLastName(),
                p.getRole().name(), p.getDateOfBirth(),
                p.getEmail(), p.getPhone(), p.getAddress()
        );
    }
}
