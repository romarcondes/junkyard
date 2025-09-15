package com.junkard.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressDTO {
	
    private Long id;
    @NotBlank(message = "Street address is required")
    private String street;
    @NotBlank(message = "City is required")
    private String city;
    @NotBlank(message = "State is required")
    private String state;
    @NotBlank(message = "Zip code is required")
    private String zipCode;
    private boolean isPrimary;
}
