package com.example.Statistics.Service;

import com.example.Statistics.DTO.Internal.DonationData;
import com.example.Statistics.DTO.Internal.ProjectData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class StatisticsService {

    private static final String DONATIONS_BY_CATEGORY = "statistics:donations:category";
    private static final String PROJECTS_BY_CATEGORY = "statistics:projects:category";
    private static final String DONATIONS_BY_CONTINENT = "statistics:donations:continent";
    private static final String PROJECTS_BY_CONTINENT = "statistics:projects:continent";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    // URLs of other microservices
    private static final String DONATION_SERVICE_URL = "http://donation-service/api/donations";
    private static final String PROJECT_SERVICE_URL = "http://project-service/api/projects";
    private static final String USER_SERVICE_URL = "http://user-service/api/users";

    public void recalculateStatistics() {
        // Fetch donations data
        try {
            DonationData[] donations = restTemplate.getForObject(DONATION_SERVICE_URL, DonationData[].class);
            if (donations != null) {
                for (DonationData donation : donations) {
                    updateDonationStats(donation.getDonor(), donation.getAmount(), donation.getCategory(), donation.getContinent());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch or process donations data: " + e.getMessage());
        }

        // Fetch projects data
        try {
            ProjectData[] projects = restTemplate.getForObject(PROJECT_SERVICE_URL, ProjectData[].class);
            if (projects != null) {
                for (ProjectData project : projects) {
                    updateProjectStats(project.getCategory(), project.getContinent());
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch or process projects data: " + e.getMessage());
        }
    }

    // Update methods
    public void updateDonationStats(String donor, double amount, String category, String continent) {
        redisTemplate.opsForHash().increment(DONATIONS_BY_CATEGORY, category, (long) amount);
        redisTemplate.opsForHash().increment(DONATIONS_BY_CONTINENT, continent, (long) amount);
    }

    public void updateProjectStats(String category, String continent) {
        redisTemplate.opsForHash().increment(PROJECTS_BY_CATEGORY, category, 1);
        redisTemplate.opsForHash().increment(PROJECTS_BY_CONTINENT, continent, 1);
    }

    // Getter methods
    public Map<Object, Object> getTotalDonationsByCategory() {
        return redisTemplate.opsForHash().entries(DONATIONS_BY_CATEGORY);
    }

    public Map<Object, Object> getTotalProjectsByCategory() {
        return redisTemplate.opsForHash().entries(PROJECTS_BY_CATEGORY);
    }

    public Map<Object, Object> getTotalDonationsByContinent() {
        return redisTemplate.opsForHash().entries(DONATIONS_BY_CONTINENT);
    }

    public Map<Object, Object> getTotalProjectsByContinent() {
        return redisTemplate.opsForHash().entries(PROJECTS_BY_CONTINENT);
    }
}