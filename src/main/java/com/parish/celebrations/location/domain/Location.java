package com.parish.celebrations.location.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "location")
public class Location {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String address;

    private Integer capacity;

    @Column(nullable = false)
    private boolean active = true;

    protected Location() {}

    public Location(String name, String address, Integer capacity) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.active = true;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public Integer getCapacity() { return capacity; }
    public boolean isActive() { return active; }
}
