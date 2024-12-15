package com.example.Statistics.Utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class SyntheticMessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.donation}")
    private String donationTopic;

    @Value("${kafka.topic.project}")
    private String projectTopic;

    public SyntheticMessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendDonationMessages(int count) {
        for (int i = 0; i < count; i++) {
            String message = String.format(
                    "{\"donor\": \"donor%d\", \"amount\": %d, \"category\": \"%s\", \"continent\": \"%s\", \"country\": \"%s\"}",
                    ThreadLocalRandom.current().nextInt(1, 10),
                    ThreadLocalRandom.current().nextInt(10, 500),
                    randomCategory(),
                    randomContinent(),
                    randomCountry()
            );
            kafkaTemplate.send(donationTopic, message);
            System.out.println("Sent donation message: " + message);
        }
    }

    public void sendProjectMessages(int count) {
        for (int i = 0; i < count; i++) {
            String message = String.format(
                    "{\"projectId\": \"project%d\", \"category\": \"%s\", \"continent\": \"%s\", \"country\": \"%s\"}",
                    ThreadLocalRandom.current().nextInt(1, 10),
                    randomCategory(),
                    randomContinent(),
                    randomCountry()
            );
            kafkaTemplate.send(projectTopic, message);
            System.out.println("Sent project message: " + message);
        }
    }

    private String randomCategory() {
        String[] categories = {"Health", "Education", "Food", "Environment"};
        return categories[ThreadLocalRandom.current().nextInt(categories.length)];
    }

    private String randomContinent() {
        String[] continents = {"Africa", "Asia", "Europe", "North America", "South America"};
        return continents[ThreadLocalRandom.current().nextInt(continents.length)];
    }

    private String randomCountry() {
        String[] countries = {"India", "USA", "Nigeria", "Brazil", "Germany"};
        return countries[ThreadLocalRandom.current().nextInt(countries.length)];
    }
}
