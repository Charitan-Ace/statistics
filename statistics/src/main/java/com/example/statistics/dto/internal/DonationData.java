// DonationData.java
package com.example.statistics.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonationData {
    private String donor;
    private double amount;
    private String category;
    private String continent;
    private String country;
    private String timestamp;
}