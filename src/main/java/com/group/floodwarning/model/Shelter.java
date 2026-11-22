package com.group.floodwarning.model;

/** A shelter and its current bed capacity. */
public record Shelter(String name, String location, int capacity, int occupied) {
    public Shelter {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Shelter name is required");
        if (location == null || location.isBlank()) throw new IllegalArgumentException("Shelter location is required");
        if (capacity < 0 || occupied < 0 || occupied > capacity) {
            throw new IllegalArgumentException("Invalid shelter capacity");
        }
    }

    public int availableBeds() {
        return capacity - occupied;
    }
}
