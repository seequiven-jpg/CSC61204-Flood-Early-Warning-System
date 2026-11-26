package com.group.floodwarning.model;

public record User(String name, String email, Role role, String passwordHash) {
    public enum Role {
        RESIDENT, ADMIN
    }
}
