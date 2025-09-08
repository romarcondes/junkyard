package com.junkard.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // O login será feito com este campo
    @Column(nullable = false, unique = true)
    private String document; 

    @Column(nullable = false)
    private String password;

    // Telefone no padrão EUA (não faremos validação de formato aqui, mas poderia ser adicionada)
    @Column(nullable = false)
    private String phone;
    
    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}