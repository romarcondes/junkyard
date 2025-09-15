package com.junkard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe central para tratar exceções em toda a aplicação.
 * Anotada com @RestControllerAdvice, ela interceta exceções lançadas pelos controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata exceções de lógica de negócio (ex: cliente sem endereço principal, documento já em uso).
     * Estas são as exceções que lançamos manualmente com 'throw new IllegalArgumentException(...)'.
     * @param ex A exceção capturada.
     * @return Uma resposta ResponseEntity com status 400 (Bad Request) e uma mensagem de erro clara.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage()); // A mensagem exata que definimos no serviço
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Trata erros de validação dos DTOs (ex: campos @NotBlank, @Email que falham).
     * @param ex A exceção lançada pelo Spring Validation.
     * @return Um mapa de campos para as suas respetivas mensagens de erro.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }

    /**
     * Um "apanha-tudo" para qualquer outra exceção inesperada.
     * Garante que nunca expomos detalhes sensíveis do sistema.
     * @param ex A exceção genérica capturada.
     * @return Uma resposta ResponseEntity com status 500 (Internal Server Error) e uma mensagem genérica e segura.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        // É uma boa prática registar o erro completo nos logs para depuração
        ex.printStackTrace();

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "An unexpected internal error occurred. Please contact support.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}