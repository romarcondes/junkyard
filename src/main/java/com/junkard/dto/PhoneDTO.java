package com.junkard.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PhoneDTO {
	private Long id;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    private String type;
    private boolean isPrimary;
}
