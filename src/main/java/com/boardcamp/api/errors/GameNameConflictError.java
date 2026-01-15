package com.boardcamp.api.errors;

public class GameNameConflictError extends RuntimeException {
    public GameNameConflictError(String message) {
        super(message);
    }
}
