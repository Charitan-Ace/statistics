// DonationDataDTO.java
package com.example.statistics.dto.external;

import lombok.Data;

@Data
public class DonationDataDTO {
    private String donor;
    private double amount;
    private String category;
    private String continent;
    private String country;
    private String timestamp;  // (ISO 8601 format)
}


