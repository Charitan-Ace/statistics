package com.example.Statistics.DTO.External;


import lombok.Data;

@Data
public class DonationDataDTO {
    private String donor;
    private double amount;
    private String category;
    private String continent;
}

