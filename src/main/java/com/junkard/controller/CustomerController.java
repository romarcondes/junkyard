package com.junkard.controller;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.junkard.dto.CustomerDTO;
import com.junkard.dto.CustomerSearchResponseDTO;
import com.junkard.model.Customer;
import com.junkard.service.CustomerService;

/**
 * Controlador REST para gerir as operações CRUD de Clientes.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/document/{document}")
    @PreAuthorize("hasAuthority('ACCESS_NEW_ORDER')") // Protegido pela mesma permissão de criar pedido
    public ResponseEntity<?> findCustomerByDocument(@PathVariable String document) {
        try {
            CustomerDTO customer = customerService.findCustomerByDocument(document);
            return ResponseEntity.ok(customer);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping
    @PreAuthorize("hasAuthority('ACCESS_NEW_CLIENT')")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerDTO customerCreateDTO) {
        try {
            Customer newCustomer = customerService.createCustomer(customerCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(newCustomer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint para procurar clientes de forma paginada.
     * Requer a permissão 'ACCESS_SEARCH_CUSTOMER'.
     */
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ACCESS_SEARCH_CUSTOMER')")
    public ResponseEntity<Page<CustomerSearchResponseDTO>> searchCustomers(
            @RequestParam(required = false, defaultValue = "") String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        
        Page<CustomerSearchResponseDTO> results = customerService.searchCustomersPaginated(term, page, size);
        return ResponseEntity.ok(results);
    }

    /**
     * Endpoint para obter os detalhes de um cliente específico pelo seu ID.
     * Requer a permissão 'ACCESS_CUSTOMER_DETAILS'.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ACCESS_CUSTOMER_DETAILS')")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        CustomerDTO customer = customerService.getCustomerById(id);
        return ResponseEntity.ok(customer);
    }

    /**
     * Endpoint para atualizar os dados de um cliente.
     * Requer a permissão 'ACCESS_CUSTOMER_DETAILS'.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ACCESS_CUSTOMER_DETAILS')")
    public ResponseEntity<?> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDTO customerDTO) {
        try {
            CustomerDTO updatedCustomerDto = customerService.updateCustomer(id, customerDTO);
            return ResponseEntity.ok(updatedCustomerDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) { // Para o caso de "Customer not found"
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}

