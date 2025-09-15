package com.junkard.dto;

import com.junkard.model.Order;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDetailDTO {
    private Long id;
    private String customerName;
    private String customerDocument;
    private String customerEmail;
    private String customerPhone;
    private String fullAddress;
    private String vehiclePlate;
    private String vehicleModel;
    private BigDecimal orderValue;
    private LocalDateTime requestDateTime;
    private LocalDateTime scheduledDateTime;
    private String status;

    // Construtor para facilitar a conversão da Entidade para DTO
    public OrderDetailDTO(Order order) {
        this.id = order.getId();
        this.customerName = order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName();
        this.customerDocument = order.getCustomer().getDocument();
        this.customerEmail = order.getCustomer().getEmail();
        this.customerPhone = order.getCustomer().getPrimaryPhone();
        this.fullAddress = String.format("%s, %s, %s - %s",
                order.getPickupAddress().getStreet(),
                order.getPickupAddress().getCity(),
                order.getPickupAddress().getState(),
                order.getPickupAddress().getZipCode());
        this.vehiclePlate = order.getVehiclePlate();
        this.vehicleModel = order.getVehicleModel();
        this.orderValue = order.getOrderValue();
        this.requestDateTime = order.getRequestDateTime();
        this.scheduledDateTime = order.getScheduledDateTime();
        this.status = order.getStatus();
    }
}
