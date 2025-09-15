package com.junkard.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * DTO para receber os dados de criação de um novo pedido.
 * Este objeto é flexível: pode conter o ID de um cliente existente
 * ou os dados para criar um novo cliente juntamente com o pedido.
 */
@Data
public class OrderCreateDTO {

    // Apenas um destes dois campos deve ser preenchido: customerId ou newCustomer.
    // A lógica para tratar isto estará na camada de serviço.
    private Long customerId;

    @Valid // Garante que, se newCustomer for fornecido, os seus campos internos sejam validados.
    private CustomerDTO newCustomer;

    @NotBlank(message = "Vehicle plate is required")
    private String vehiclePlate;

    @NotBlank(message = "Vehicle model is required")
    private String vehicleModel;

    @NotNull(message = "Order value is required")
    @Positive(message = "Order value must be a positive number")
    private BigDecimal orderValue;
    
    
    @NotNull(message = "Scheduled date and time is required")
    @Future(message = "Scheduled date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm") // Define o formato esperado do frontend
    private LocalDateTime scheduledDateTime;
}
