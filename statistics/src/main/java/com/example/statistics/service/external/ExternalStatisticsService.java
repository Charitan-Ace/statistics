// com.example.statistics.service.external.ExternalStatisticsService.java
package com.example.statistics.service.external;

import com.example.statistics.dto.external.DonationDataDTO;
import com.example.statistics.dto.external.ProjectDataDTO;
import com.example.statistics.dto.internal.DonationData;
import com.example.statistics.dto.internal.ProjectData;
import com.example.statistics.service.internal.StatisticsServiceInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ExternalStatisticsService implements ExternalStatisticsServiceInterface {

    private final StatisticsServiceInterface statisticsService;
    private final ObjectMapper objectMapper;

    public ExternalStatisticsService(StatisticsServiceInterface statisticsService) {
        this.statisticsService = statisticsService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    @KafkaListener(topics = "donation-service-topic", groupId = "statistics-group")
    public void processDonation(DonationDataDTO donationDataDTO) {
        try {
            System.out.println("Received Donation Data: " + donationDataDTO);
            // Convert External DTO to Internal DTO
            DonationData donationData = new DonationData(
                    donationDataDTO.getDonor(),
                    donationDataDTO.getAmount(),
                    donationDataDTO.getCategory(),
                    donationDataDTO.getContinent(),
                    donationDataDTO.getCountry(),
                    donationDataDTO.getTimestamp()
            );
            // Delegate to internal service
            statisticsService.updateDonationStats(
                    donationData.getDonor(),
                    donationData.getAmount(),
                    donationData.getCategory(),
                    donationData.getContinent(),
                    donationData.getCountry(),
                    donationData.getTimestamp()
            );
        } catch (Exception e) {
            System.err.println("Failed to process donation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    @KafkaListener(topics = "project-service-topic", groupId = "statistics-group")
    public void processProject(ProjectDataDTO projectDataDTO) {
        try {
            System.out.println("Received Project Data: " + projectDataDTO);
            // Convert External DTO to Internal DTO
            ProjectData projectData = new ProjectData(
                    projectDataDTO.getCategory(),
                    projectDataDTO.getContinent(),
                    projectDataDTO.getCountry(),
                    projectDataDTO.getStartTime(),
                    projectDataDTO.getUsername()
            );
            // Delegate to internal service
            statisticsService.updateProjectStats(
                    projectData.getCategory(),
                    projectData.getContinent(),
                    projectData.getCountry(),
                    projectData.getStartTime(),
                    projectData.getUsername()
            );
        } catch (Exception e) {
            System.err.println("Failed to process project: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
