package com.junkard.service;

import com.junkard.dto.OrderCreateDTO;
import com.junkard.dto.OrderDetailDTO;
import com.junkard.dto.OrderSearchResponseDTO;
import com.junkard.model.Address;
import com.junkard.model.Customer;
import com.junkard.model.Order;
import com.junkard.repository.CustomerRepository;
import com.junkard.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService; // Reutiliza o serviço de cliente

    @Transactional
    public Order createOrder(OrderCreateDTO dto) {
        Customer customer;

        // Passo 1: Determinar o cliente (existente ou novo)
        if (dto.getCustomerId() != null) {
            customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + dto.getCustomerId()));
        } else if (dto.getNewCustomer() != null) {
            customer = customerService.createCustomer(dto.getNewCustomer());
        } else {
            throw new IllegalArgumentException("Customer information (customerId or newCustomer) is required.");
        }

        // Passo 2: Encontrar o endereço principal do cliente
        Address primaryAddress = customer.getAddresses().stream()
                .filter(Address::isPrimary)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("The selected customer does not have a primary address."));

        // Passo 3: Criar e preencher a entidade Order
        Order order = new Order();
        order.setCustomer(customer);
        order.setPickupAddress(primaryAddress);
        order.setVehiclePlate(dto.getVehiclePlate());
        order.setVehicleModel(dto.getVehicleModel());
        order.setOrderValue(dto.getOrderValue());
        order.setScheduledDateTime(dto.getScheduledDateTime());
        order.setStatus("SCHEDULED");

        // Passo 4: Salvar e devolver o novo pedido
        return orderRepository.save(order);
    }

    /**
     * Procura pedidos com base em filtros opcionais, com paginação e ordenação.
     */
    @Transactional(readOnly = true)
    public Page<OrderSearchResponseDTO> searchOrders(String status, LocalDate requestDate, LocalDate scheduledDate, Pageable pageable) {
        
        Specification<Order> spec = Specification.where(null);

        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (requestDate != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("requestDateTime"), requestDate.atStartOfDay(), requestDate.atTime(23, 59, 59)));
        }
        if (scheduledDate != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("scheduledDateTime"), scheduledDate.atStartOfDay(), scheduledDate.atTime(23, 59, 59)));
        }

        Page<Order> ordersPage = orderRepository.findAll(spec, pageable);

        return ordersPage.map(this::convertOrderToSearchDTO);
    }
    
    /**
     * Busca os detalhes completos de um único pedido pelo seu ID.
     */
    @Transactional(readOnly = true)
    public OrderDetailDTO getOrderDetailsById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        return new OrderDetailDTO(order);
    }

    /**
     * Atualiza o status de um pedido.
     */
    @Transactional
    public Order updateOrderStatus(Long id, String newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        order.setStatus(newStatus.toUpperCase());
        return orderRepository.save(order);
    }
    
    // Método auxiliar para converter a entidade Order para o DTO de resposta da busca
    private OrderSearchResponseDTO convertOrderToSearchDTO(Order order) {
        Address addr = order.getPickupAddress();
        String fullAddress = String.format("%s, %s, %s - %s", addr.getStreet(), addr.getCity(), addr.getState(), addr.getZipCode());
        
        return new OrderSearchResponseDTO(
            order.getId(),
            order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName(),
            order.getCustomer().getDocument(),
            order.getStatus(),
            order.getRequestDateTime(),
            order.getScheduledDateTime(),
            fullAddress
        );
    }
}

