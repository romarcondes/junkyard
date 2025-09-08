package com.junkard.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Esta anotação torna a classe um componente global para tratar exceções em RestControllers.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Esta anotação indica que este método vai tratar exceções do tipo MethodArgumentNotValidException.
    // É exatamente essa exceção que o Spring lança quando a validação de um @Valid falha.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Define o status HTTP da resposta para 400
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField(); // Pega o nome do campo (ex: "password")
            String errorMessage = error.getDefaultMessage(); // Pega a mensagem do erro (ex: "Password must have at least 6 characters")
            errors.put(fieldName, errorMessage);
        });

        // Retorna o mapa de erros, que será convertido para JSON
        return errors;
    }
}