package com.ajmv.altoValeNewsBackend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandlingController {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingController.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        logger.error("An error occurred during file processing", e);
        return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
    }
}