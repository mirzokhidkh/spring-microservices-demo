package com.mk.customer;

public record CustomerRegistrationRequest(
        String firstName,
        String lastName,
        String email) {
}
