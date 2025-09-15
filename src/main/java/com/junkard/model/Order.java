package com.junkard.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private String vehiclePlate;

    @Column(nullable = false)
    private String vehicleModel;

    @Column(nullable = false)
    private BigDecimal orderValue;

    @Column(nullable = false)
    private LocalDateTime scheduledDateTime;

    @Column(nullable = false, updatable = false)
    private LocalDateTime requestDateTime;

    @Column(nullable = false)
    private String status;
    
    // Armazena uma cópia do endereço principal no momento da criação do pedido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_address_id", nullable = false)
    private Address pickupAddress;

    // Define a data do pedido no momento da criação
    @PrePersist
    protected void onCreate() {
        requestDateTime = LocalDateTime.now();
        status = "Scheduled"; // O pedido nasce sempre como 'Scheduled'
    }
}
