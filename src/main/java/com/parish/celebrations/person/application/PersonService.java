package com.parish.celebrations.person.application;

import com.parish.celebrations.person.domain.Person;
import com.parish.celebrations.person.domain.PersonRepository;
import com.parish.celebrations.person.domain.PersonRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class PersonService {

    private final PersonRepository repo;

    public PersonService(PersonRepository repo) { this.repo = repo; }

    public Person findById(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Person not found"));
    }

    public Page<Person> findAll(String q, String role, Pageable pageable) {
        if (q != null && q.length() >= 2) return repo.search(q, pageable);
        if (role != null) return repo.findByRole(PersonRole.valueOf(role), pageable);
        return repo.findAll(pageable);
    }

    public Person create(String firstName, String lastName, String role) {
        Person p = new Person(firstName, lastName, PersonRole.valueOf(role));
        return repo.save(p);
    }
}
