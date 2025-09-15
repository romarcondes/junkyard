package com.junkard.dto;

import lombok.Data;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class CustomerDTO {
    private Long id;

    @NotBlank @Size(min = 2, max = 50)
    private String firstName;
    
    @NotBlank @Size(min = 2, max = 50)
    private String lastName;
    
    @NotBlank @Email
    private String email;

    @NotBlank
    private String document;

    @Valid // Garante que os objetos dentro da lista são validados
    @NotEmpty(message = "At least one phone is required")
    private List<PhoneDTO> phones;
    
    @Valid // Garante que os objetos dentro da lista são validados
    @NotEmpty(message = "At least one address is required")
    private List<AddressDTO> addresses;
}

