package com.parish.celebrations.person.api;

import com.parish.celebrations.person.api.dto.CreatePersonRequest;
import com.parish.celebrations.person.api.dto.PersonResponse;
import com.parish.celebrations.person.application.PersonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    private final PersonService svc;

    public PersonController(PersonService svc) { this.svc = svc; }

    @GetMapping("/{id}")
    public PersonResponse findById(@PathVariable UUID id) {
        return PersonResponse.from(svc.findById(id));
    }

    @GetMapping
    public Page<PersonResponse> findAll(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("lastName"));
        return svc.findAll(q, role, pageable).map(PersonResponse::from);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonResponse create(@RequestBody CreatePersonRequest req) {
        return PersonResponse.from(svc.create(req.firstName(), req.lastName(), req.role()));
    }
}
