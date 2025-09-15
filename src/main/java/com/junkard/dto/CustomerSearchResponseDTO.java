package com.junkard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSearchResponseDTO {
	private Long id;
	private String fullName;
    private String email;
    private String phone;
    private String document;
    
}
