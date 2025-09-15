package com.junkard.controller;

import com.junkard.dto.OrderCreateDTO;
import com.junkard.dto.OrderDetailDTO;
import com.junkard.dto.OrderSearchResponseDTO;
import com.junkard.model.Order;
import com.junkard.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAuthority('ACCESS_NEW_ORDER')")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody OrderCreateDTO orderCreateDTO) {
        Order newOrder = orderService.createOrder(orderCreateDTO);
        return ResponseEntity.ok(newOrder);
    }

    /**
     * Procura pedidos de forma paginada e com filtros opcionais.
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ACCESS_ORDER_SEARCH')")
    public ResponseEntity<Page<OrderSearchResponseDTO>> searchOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate requestDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate scheduledDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        // Ordena por data de agendamento, do mais recente para o mais antigo, por defeito
        Pageable pageable = PageRequest.of(page, size, Sort.by("scheduledDateTime").descending());
        
        Page<OrderSearchResponseDTO> results = orderService.searchOrders(status, requestDate, scheduledDate, pageable);
        return ResponseEntity.ok(results);
    }

    /**
     * Busca os detalhes de um pedido específico.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACCESS_ORDER_DETAILS')")
    public ResponseEntity<OrderDetailDTO> getOrderById(@PathVariable Long id) {
        OrderDetailDTO orderDetails = orderService.getOrderDetailsById(id);
        return ResponseEntity.ok(orderDetails);
    }

    /**
     * Atualiza o status de um pedido (ex: para COMPLETED ou CANCELLED).
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ACCESS_ORDER_DETAILS')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate) {
        try {
            String newStatus = statusUpdate.get("status");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "New status is required."));
            }
            orderService.updateOrderStatus(id, newStatus);
            return ResponseEntity.ok(Map.of("message", "Order status updated successfully to " + newStatus));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

