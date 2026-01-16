package com.boardcamp.api.errors;

public class RentalNotFoundError extends RuntimeException {
    public RentalNotFoundError(String message) {
        super(message);
    }
}
