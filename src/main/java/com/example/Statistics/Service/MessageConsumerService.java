package com.example.Statistics.Service;

import com.example.Statistics.DTO.External.DonationDataDTO;
import com.example.Statistics.DTO.External.ProjectDataDTO;
import com.example.Statistics.DTO.Internal.DonationData;
import com.example.Statistics.DTO.Internal.ProjectData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumerService {

    private final JwtService jwtService;
    private final StatisticsService statisticsService;
    private final ObjectMapper objectMapper;

    public MessageConsumerService(JwtService jwtService, StatisticsService statisticsService) {
        this.jwtService = jwtService;
        this.statisticsService = statisticsService;
        this.objectMapper = new ObjectMapper();
    }

    @KafkaListener(topics = "onSigKeyUpdate", groupId = "key-management-group")
    public void consumeKeyUpdateMessage(String message) {
        try {
            String publicKey = parseValue(message, "publicKey");
            jwtService.updatePublicKey(publicKey);
            System.out.println("Public key updated in JwtService.");
        } catch (Exception e) {
            System.err.println("Failed to process key update: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "donation-service-topic", groupId = "statistics-group")
    public void processDonation(String message) {
        try {
            System.out.println("Received Donation Data: " + message);

            DonationDataDTO donationDTO = objectMapper.readValue(message, DonationDataDTO.class);
            DonationData donationData = new DonationData(
                    donationDTO.getDonor(),
                    donationDTO.getAmount(),
                    donationDTO.getCategory(),
                    donationDTO.getContinent()
            );

            statisticsService.updateDonationStats(
                    donationData.getDonor(),
                    donationData.getAmount(),
                    donationData.getCategory(),
                    donationData.getContinent()
            );
        } catch (Exception e) {
            System.err.println("Failed to process donation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @KafkaListener(topics = "project-service-topic", groupId = "statistics-group")
    public void processProject(String message) {
        try {
            System.out.println("Received Project Data: " + message);

            ProjectDataDTO projectDTO = objectMapper.readValue(message, ProjectDataDTO.class);
            ProjectData projectData = new ProjectData(
                    projectDTO.getCategory(),
                    projectDTO.getContinent()
            );

            statisticsService.updateProjectStats(
                    projectData.getCategory(),
                    projectData.getContinent()
            );
        } catch (Exception e) {
            System.err.println("Failed to process project: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String parseValue(String message, String key) throws Exception {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            JsonNode valueNode = jsonNode.get(key);

            if (valueNode == null || valueNode.isNull()) {
                throw new Exception("Missing or null value for key: " + key);
            }
            return valueNode.asText();
        } catch (Exception e) {
            throw new Exception("Failed to parse key '" + key + "' from message: " + message, e);
        }
    }
}