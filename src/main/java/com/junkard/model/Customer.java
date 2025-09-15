package com.junkard.model;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String document;
    
    // Relação Um-para-Muitos com Telefones
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Phone> phones = new ArrayList<>();

    // Relação Um-para-Muitos com Endereços
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Address> addresses = new ArrayList<>();

    // Métodos auxiliares para obter o telefone e endereço principal
    public String getPrimaryPhone() {
        return phones.stream()
            .filter(Phone::isPrimary)
            .findFirst()
            .map(Phone::getPhoneNumber)
            .orElse(phones.isEmpty() ? "N/A" : phones.get(0).getPhoneNumber());
    }
}
