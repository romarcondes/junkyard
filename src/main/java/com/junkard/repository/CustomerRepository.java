package com.junkard.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.junkard.model.Customer;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByDocument(String document);
    boolean existsByEmail(String email);
    
    Optional<Customer> findByDocument(String document);
    
    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :term, '%')) OR " +
            "c.document LIKE CONCAT('%', :term, '%')")
     Page<Customer> searchByTerm(String term, Pageable pageable);
    
    
}
