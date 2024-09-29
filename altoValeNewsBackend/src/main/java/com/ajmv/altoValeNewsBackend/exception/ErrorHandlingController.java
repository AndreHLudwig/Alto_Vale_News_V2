package com.ajmv.altoValeNewsBackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class ErrorHandlingController {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingController.class);

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<String> handleFileNotFoundException(FileNotFoundException e) {
        logger.warn("File not found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("O arquivo solicitado não foi encontrado. Ele pode ter sido removido ou movido.");
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<String> handleFileProcessingException(FileProcessingException e) {
        logger.error("Error processing file: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocorreu um erro ao processar a solicitação do arquivo.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        logger.error("An unexpected error occurred: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.");
    }
}