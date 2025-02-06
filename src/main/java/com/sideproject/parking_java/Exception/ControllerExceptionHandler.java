package com.sideproject.parking_java.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(AuthenticationError.class)
    public ResponseEntity<String> AuthenticationError(AuthenticationError exception) {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("AuthenticationError: " + exception.getMessage());
        // ResponseEntity<String> response = new ResponseEntity<>("AuthenticationError: " + exception.getMessage(), HttpStatus.UNAUTHORIZED);
        
        return response;
    }

    @ExceptionHandler(DatabaseError.class)
    public ResponseEntity<String> DatabaseError(DatabaseError exception) {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("DatabaseError: " + exception.getMessage());
        
        return response;
    }

    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<String> InternalServerError(InternalServerError exception) {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("InternalServerError: " + exception.getMessage());
        
        return response;
    }

    @ExceptionHandler(InvalidParameterError.class)
    public ResponseEntity<String> InvalidParameterError(InvalidParameterError exception) {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).body("InvalidParameterError: " + exception.getMessage());
        
        return response;
    }

    @ExceptionHandler(S3Error.class)
    public ResponseEntity<String> S3Error(S3Error exception) {
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("S3Error: " + exception.getMessage());
        
        return response;
    }
}
