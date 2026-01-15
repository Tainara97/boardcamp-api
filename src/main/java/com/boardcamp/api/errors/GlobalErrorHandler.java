package com.boardcamp.api.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalErrorHandler {
    
    @ExceptionHandler({GameNameConflictError.class}) 
    public ResponseEntity<String> handleGameNameConlict(GameNameConflictError error) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error.getMessage());
    }

    @ExceptionHandler({CustomerCpfConflictError.class})
    public ResponseEntity<String> handleCustomerCpfConflict(CustomerCpfConflictError error) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error.getMessage());
    }

    @ExceptionHandler({CustomerNotFoundError.class})
    public ResponseEntity<String> handleCustomerNotFound(CustomerNotFoundError error) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error.getMessage());
    }
}
