package com.example.Statistics.DTO.Internal;

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
}
