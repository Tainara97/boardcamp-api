package com.boardcamp.api.errors;

public class GameUnavailableError extends RuntimeException {
    public GameUnavailableError(String message) {
        super(message);
    }
}