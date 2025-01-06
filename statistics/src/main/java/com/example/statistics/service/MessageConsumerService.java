// com.example.statistics.service.MessageConsumerService.java
package com.example.statistics.service;

import com.example.statistics.dto.external.DonationDataDTO;
import com.example.statistics.dto.external.ProjectDataDTO;
import com.example.statistics.dto.internal.DonationData;
import com.example.statistics.dto.internal.ProjectData;
import com.example.statistics.service.internal.StatisticsServiceInterface;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumerService {

    private final JwtService jwtService;
    private final StatisticsServiceInterface statisticsService;
    private final ObjectMapper objectMapper;

    public MessageConsumerService(JwtService jwtService, StatisticsServiceInterface statisticsService) {
        this.jwtService = jwtService;
        this.statisticsService = statisticsService;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Listens to the "onSigKeyUpdate" Kafka topic to update the JWT public key.
     *
     * @param message The incoming message containing the new public key.
     */
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

    /**
     * Listens to the "donation-service-topic" Kafka topic to process new donations.
     *
     * @param message The incoming donation data in JSON format.
     */
    @KafkaListener(topics = "donation-service-topic", groupId = "statistics-group")
    public void processDonation(String message) {
        try {
            System.out.println("Received Donation Data: " + message);

            // Parse the message into DonationDataDTO
            DonationDataDTO donationDTO = objectMapper.readValue(message, DonationDataDTO.class);

            // Create a DonationData object, including all fields
            DonationData donationData = new DonationData(
                    donationDTO.getDonor(),
                    donationDTO.getAmount(),
                    donationDTO.getCategory(),
                    donationDTO.getContinent(),
                    donationDTO.getCountry(),
                    donationDTO.getTimestamp()
            );

            // Update statistics using the full DonationData object
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

    /**
     * Listens to the "project-service-topic" Kafka topic to process new projects.
     *
     * @param message The incoming project data in JSON format.
     */
    @KafkaListener(topics = "project-service-topic", groupId = "statistics-group")
    public void processProject(String message) {
        try {
            System.out.println("Received Project Data: " + message);

            // Deserialize the message into ProjectDataDTO
            ProjectDataDTO projectDTO = objectMapper.readValue(message, ProjectDataDTO.class);

            // Create a ProjectData object, providing all required fields
            ProjectData projectData = new ProjectData(
                    projectDTO.getCategory(),
                    projectDTO.getContinent(),
                    projectDTO.getCountry(),
                    projectDTO.getStartTime(),
                    projectDTO.getUsername() // Ensure username is included
            );

            // Update statistics using the complete ProjectData object
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

    /**
     * Helper method to parse a specific key from a JSON message.
     *
     * @param message The JSON message as a string.
     * @param key     The key to extract the value for.
     * @return The value associated with the specified key.
     * @throws Exception If parsing fails or the key is missing/null.
     */
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
