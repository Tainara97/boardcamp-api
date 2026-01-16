package com.boardcamp.api.errors;

public class GameNotFoundError extends RuntimeException {
    public GameNotFoundError(String message) {
        super(message);
    }
}
