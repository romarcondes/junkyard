package com.junkard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchResponseDTO {
    private Long id;
    private String customerName;
    private String customerDocument;
    private String status;
    private LocalDateTime requestDateTime;
    private LocalDateTime scheduledDateTime;
    private String fullAddress;
}
