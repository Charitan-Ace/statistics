//// StatisticsService.java
//package com.example.Statistics.Service;
//
//import com.example.Statistics.dto.Internal.DonationData;
//import com.example.Statistics.dto.Internal.ProjectData;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//@Service
//public class StatisticsService {
//
//    // Existing Redis keys
//    private static final String DONATIONS_BY_CATEGORY = "statistics:donations:category";
//    private static final String PROJECTS_BY_CATEGORY = "statistics:projects:category";
//    private static final String DONATIONS_BY_CONTINENT = "statistics:donations:continent";
//    private static final String PROJECTS_BY_CONTINENT = "statistics:projects:continent";
//    private static final String DONATIONS_BY_COUNTRY = "statistics:donations:country";      // New
//    private static final String PROJECTS_BY_COUNTRY = "statistics:projects:country";        // New
//    private static final String DONATIONS_BY_DATE = "statistics:donations:date";            // New
//    private static final String PROJECTS_BY_DATE = "statistics:projects:date";              // New
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    // URLs of other microservices
//    private static final String DONATION_SERVICE_URL = "http://donation-service/api/donations";
//    private static final String PROJECT_SERVICE_URL = "http://project-service/api/projects";
//
//    public void recalculateStatistics() {
//        // Fetch donations data
//        try {
//            DonationData[] donations = restTemplate.getForObject(DONATION_SERVICE_URL, DonationData[].class);
//            if (donations != null) {
//                for (DonationData donation : donations) {
//                    updateDonationStats(
//                            donation.getDonor(),
//                            donation.getAmount(),
//                            donation.getCategory(),
//                            donation.getContinent(),
//                            donation.getCountry(),
//                            donation.getTimestamp()
//                    );
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Failed to fetch or process donations data: " + e.getMessage());
//        }
//
//        // Fetch projects data
//        try {
//            ProjectData[] projects = restTemplate.getForObject(PROJECT_SERVICE_URL, ProjectData[].class);
//            if (projects != null) {
//                for (ProjectData project : projects) {
//                    updateProjectStats(
//                            project.getCategory(),
//                            project.getContinent(),
//                            project.getCountry(),
//                            project.getStartTime()
//                    );
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Failed to fetch or process projects data: " + e.getMessage());
//        }
//    }
//
//    // Update methods
//    public void updateDonationStats(String donor, double amount, String category, String continent, String country, String timestamp) {
//        redisTemplate.opsForHash().increment(DONATIONS_BY_CATEGORY, category, (long) amount);
//        redisTemplate.opsForHash().increment(DONATIONS_BY_CONTINENT, continent, (long) amount);
//        redisTemplate.opsForHash().increment(DONATIONS_BY_COUNTRY, country, (long) amount);
//
//        String date = extractDate(timestamp);
//        redisTemplate.opsForHash().increment(DONATIONS_BY_DATE, date, (long) amount);
//    }
//
//    public void updateProjectStats(String category, String continent, String country, String startTime) {
//        redisTemplate.opsForHash().increment(PROJECTS_BY_CATEGORY, category, 1);
//        redisTemplate.opsForHash().increment(PROJECTS_BY_CONTINENT, continent, 1);
//        redisTemplate.opsForHash().increment(PROJECTS_BY_COUNTRY, country, 1);
//
//        String date = extractDate(startTime);
//        redisTemplate.opsForHash().increment(PROJECTS_BY_DATE, date, 1);
//    }
//
//    private String extractDate(String timestamp) {
//        // Assuming timestamp is in ISO 8601 format
//        LocalDate date = LocalDate.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
//        return date.toString(); // e.g., "2024-12-29"
//    }
//
//    // Getter methods
//    public Map<Object, Object> getTotalDonationsByCategory() {
//        return redisTemplate.opsForHash().entries(DONATIONS_BY_CATEGORY);
//    }
//
//    public Map<Object, Object> getTotalProjectsByCategory() {
//        return redisTemplate.opsForHash().entries(PROJECTS_BY_CATEGORY);
//    }
//
//    public Map<Object, Object> getTotalDonationsByContinent() {
//        return redisTemplate.opsForHash().entries(DONATIONS_BY_CONTINENT);
//    }
//
//    public Map<Object, Object> getTotalProjectsByContinent() {
//        return redisTemplate.opsForHash().entries(PROJECTS_BY_CONTINENT);
//    }
//
//    // New methods for filtered statistics
//    public Map<String, Object> getFilteredStatistics(Optional<String> continent,
//                                                     Optional<String> country,
//                                                     Optional<String> category,
//                                                     Optional<String> startDate,
//                                                     Optional<String> endDate) {
//        Map<String, Object> result = new HashMap<>();
//
//        // Example: Implement filtering logic based on provided parameters
//        // This can be customized based on specific requirements
//
//        if (continent.isPresent()) {
//            Map<Object, Object> donationsByContinent = redisTemplate.opsForHash().entries(DONATIONS_BY_CONTINENT);
//            result.put("donationsByContinent", donationsByContinent.get(continent.get()));
//        }
//
//        if (country.isPresent()) {
//            Map<Object, Object> donationsByCountry = redisTemplate.opsForHash().entries(DONATIONS_BY_COUNTRY);
//            result.put("donationsByCountry", donationsByCountry.get(country.get()));
//        }
//
//        if (category.isPresent()) {
//            Map<Object, Object> donationsByCategory = redisTemplate.opsForHash().entries(DONATIONS_BY_CATEGORY);
//            result.put("donationsByCategory", donationsByCategory.get(category.get()));
//        }
//
//        if (startDate.isPresent() && endDate.isPresent()) {
//            // Fetch donations within the date range
//            Map<Object, Object> donationsByDate = redisTemplate.opsForHash().entries(DONATIONS_BY_DATE);
//            long total = 0;
//            for (Map.Entry<Object, Object> entry : donationsByDate.entrySet()) {
//                String date = (String) entry.getKey();
//                if (date.compareTo(startDate.get()) >= 0 && date.compareTo(endDate.get()) <= 0) {
//                    total += (Long) entry.getValue();
//                }
//            }
//            result.put("donationsByDateRange", total);
//        }
//
//        return result;
//    }
//}
