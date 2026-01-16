package com.boardcamp.api.errors;

public class RentalAlreadyReturnedError extends RuntimeException{
    public RentalAlreadyReturnedError(String message) {
        super(message);
    }
}
