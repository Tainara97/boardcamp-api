package com.boardcamp.api.errors;

public class CustomerCpfConflictError extends RuntimeException  {
    public CustomerCpfConflictError(String message) {
        super(message);
    }
}
