package com.boardcamp.api.errors;

public class RentalNotReturnedError extends RuntimeException {
    public RentalNotReturnedError(String message) {
        super(message);
    }
}
