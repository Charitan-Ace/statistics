// com.example.statistics.service.internal.StatisticsService.java
package com.example.statistics.service.internal;

import com.example.statistics.dto.internal.DonationData;
import com.example.statistics.dto.internal.ProjectData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class StatisticsService implements StatisticsServiceInterface {

    // Redis keys
    private static final String DONATIONS_BY_CATEGORY = "statistics:donations:category";
    private static final String PROJECTS_BY_CATEGORY = "statistics:projects:category";
    private static final String DONATIONS_BY_CONTINENT = "statistics:donations:continent";
    private static final String PROJECTS_BY_CONTINENT = "statistics:projects:continent";
    private static final String DONATIONS_BY_COUNTRY = "statistics:donations:country";
    private static final String PROJECTS_BY_COUNTRY = "statistics:projects:country";
    private static final String DONATIONS_BY_DATE = "statistics:donations:date";
    private static final String PROJECTS_BY_DATE = "statistics:projects:date";

    // Redis keys for totals
    private static final String DONATIONS_TOTAL_PREFIX = "statistics:donations:total:user:"; // e.g., statistics:donations:total:user:username
    private static final String PROJECTS_TOTAL_PREFIX = "statistics:projects:total:user:";   // e.g., statistics:projects:total:user:username

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    // URLs of other microservices
    private static final String DONATION_SERVICE_URL = "http://donation-service/api/donations";
    private static final String PROJECT_SERVICE_URL = "http://project-service/api/projects";

    @Override
    public void recalculateStatistics() {
        // Fetch donations data
        try {
            DonationData[] donations = restTemplate.getForObject(DONATION_SERVICE_URL, DonationData[].class);
            if (donations != null) {
                for (DonationData donation : donations) {
                    updateDonationStats(
                            donation.getDonor(),
                            donation.getAmount(),
                            donation.getCategory(),
                            donation.getContinent(),
                            donation.getCountry(),
                            donation.getTimestamp()
                    );
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
                    updateProjectStats(
                            project.getCategory(),
                            project.getContinent(),
                            project.getCountry(),
                            project.getStartTime(),
                            project.getUsername()
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch or process projects data: " + e.getMessage());
        }
    }

    // Update methods
    @Override
    public void updateDonationStats(String donor, double amount, String category, String continent, String country, String timestamp) {
        // Update by category
        redisTemplate.opsForHash().increment(DONATIONS_BY_CATEGORY, category, (long) amount);
        // Update by continent
        redisTemplate.opsForHash().increment(DONATIONS_BY_CONTINENT, continent, (long) amount);
        // Update by country
        redisTemplate.opsForHash().increment(DONATIONS_BY_COUNTRY, country, (long) amount);
        // Update by date
        String date = extractDate(timestamp);
        redisTemplate.opsForHash().increment(DONATIONS_BY_DATE, date, (long) amount);

        // Update per-user total donations using SCAN
        String userTotalKey = DONATIONS_TOTAL_PREFIX + donor;
        redisTemplate.opsForValue().increment(userTotalKey, amount);
    }

    @Override
    public void updateProjectStats(String category, String continent, String country, String startTime, String username) {
        // Update by category
        redisTemplate.opsForHash().increment(PROJECTS_BY_CATEGORY, category, 1);
        // Update by continent
        redisTemplate.opsForHash().increment(PROJECTS_BY_CONTINENT, continent, 1);
        // Update by country
        redisTemplate.opsForHash().increment(PROJECTS_BY_COUNTRY, country, 1);
        // Update by date
        String date = extractDate(startTime);
        redisTemplate.opsForHash().increment(PROJECTS_BY_DATE, date, 1);

        // Update per-user total projects using SCAN
        String userTotalKey = PROJECTS_TOTAL_PREFIX + username;
        redisTemplate.opsForValue().increment(userTotalKey, 1);
    }

    private String extractDate(String timestamp) {
        // Assuming timestamp is in ISO 8601 format
        LocalDate date = LocalDate.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
        return date.toString(); // e.g., "2024-12-29"
    }

    // Getter methods using SCAN

    @Override
    public double getTotalDonations() {
        double total = 0.0;
        ScanOptions options = ScanOptions.scanOptions()
                .match("statistics:donations:total:user:*")
                .count(1000)
                .build();
        try (Cursor<byte[]> cursor = redisTemplate.executeWithStickyConnection(
                redisConnection -> redisConnection.scan(options)
        )) {
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                Object value = redisTemplate.opsForValue().get(key);
                if (value instanceof Number) {
                    total += ((Number) value).doubleValue();
                }
            }
        } catch (Exception e) {
            System.err.println("Error during SCAN for total donations: " + e.getMessage());
        }
        return total;
    }

    @Override
    public long getTotalProjects() {
        long total = 0L;
        ScanOptions options = ScanOptions.scanOptions()
                .match("statistics:projects:total:user:*")
                .count(1000)
                .build();
        try (Cursor<byte[]> cursor = redisTemplate.executeWithStickyConnection(
                redisConnection -> redisConnection.scan(options)
        )) {
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                Object value = redisTemplate.opsForValue().get(key);
                if (value instanceof Number) {
                    total += ((Number) value).longValue();
                }
            }
        } catch (Exception e) {
            System.err.println("Error during SCAN for total projects: " + e.getMessage());
        }
        return total;
    }

    @Override
    public double getTotalDonationsForUser(String username) {
        String userTotalKey = DONATIONS_TOTAL_PREFIX + username;
        Object total = redisTemplate.opsForValue().get(userTotalKey);
        if (total instanceof Number) {
            return ((Number) total).doubleValue();
        }
        return 0.0;
    }

    @Override
    public long getTotalProjectsForUser(String username) {
        String userTotalKey = PROJECTS_TOTAL_PREFIX + username;
        Object total = redisTemplate.opsForValue().get(userTotalKey);
        if (total instanceof Number) {
            return ((Number) total).longValue();
        }
        return 0L;
    }

    @Override
    public Map<Object, Object> getTotalDonationsByCategory() {
        return redisTemplate.opsForHash().entries(DONATIONS_BY_CATEGORY);
    }

    @Override
    public Map<Object, Object> getTotalProjectsByCategory() {
        return redisTemplate.opsForHash().entries(PROJECTS_BY_CATEGORY);
    }

    @Override
    public Map<Object, Object> getTotalDonationsByContinent() {
        return redisTemplate.opsForHash().entries(DONATIONS_BY_CONTINENT);
    }

    @Override
    public Map<Object, Object> getTotalProjectsByContinent() {
        return redisTemplate.opsForHash().entries(PROJECTS_BY_CONTINENT);
    }

    @Override
    public Map<Object, Object> getTotalDonationsByCountry() {
        return redisTemplate.opsForHash().entries(DONATIONS_BY_COUNTRY);
    }

    @Override
    public Map<Object, Object> getTotalProjectsByCountry() {
        return redisTemplate.opsForHash().entries(PROJECTS_BY_COUNTRY);
    }

    @Override
    public Map<Object, Object> getTotalDonationsByDate() {
        return redisTemplate.opsForHash().entries(DONATIONS_BY_DATE);
    }

    @Override
    public Map<Object, Object> getTotalProjectsByDate() {
        return redisTemplate.opsForHash().entries(PROJECTS_BY_DATE);
    }

    // User-specific statistics
    @Override
    public Map<Object, Object> getDonationsByCategoryForUser(String username) {
        // Assuming per-user donations by category are stored under separate keys
        String userCategoryKey = "statistics:donations:category:user:" + username;
        return redisTemplate.opsForHash().entries(userCategoryKey);
    }

    @Override
    public Map<Object, Object> getProjectsByCategoryForUser(String username) {
        // Similarly, assuming per-user projects by category are stored
        String userCategoryKey = "statistics:projects:category:user:" + username;
        return redisTemplate.opsForHash().entries(userCategoryKey);
    }

    // Implement filtered statistics as per existing method
    @Override
    public Map<String, Object> getFilteredStatistics(Optional<String> continent, Optional<String> country,
                                                     Optional<String> category, Optional<String> startDate,
                                                     Optional<String> endDate, Optional<String> username) {
        Map<String, Object> result = new HashMap<>();

        if (continent.isPresent()) {
            Map<Object, Object> donationsByContinent = redisTemplate.opsForHash().entries(DONATIONS_BY_CONTINENT);
            Object value = donationsByContinent.get(continent.get());
            result.put("donationsByContinent", value != null ? value : 0L);
        }

        if (country.isPresent()) {
            Map<Object, Object> donationsByCountry = redisTemplate.opsForHash().entries(DONATIONS_BY_COUNTRY);
            Object value = donationsByCountry.get(country.get());
            result.put("donationsByCountry", value != null ? value : 0L);
        }

        if (category.isPresent()) {
            Map<Object, Object> donationsByCategory = redisTemplate.opsForHash().entries(DONATIONS_BY_CATEGORY);
            Object value = donationsByCategory.get(category.get());
            result.put("donationsByCategory", value != null ? value : 0L);
        }

        if (startDate.isPresent() && endDate.isPresent()) {
            // Fetch donations within the date range
            Map<Object, Object> donationsByDate = redisTemplate.opsForHash().entries(DONATIONS_BY_DATE);
            long total = 0;
            for (Map.Entry<Object, Object> entry : donationsByDate.entrySet()) {
                String date = (String) entry.getKey();
                if (date.compareTo(startDate.get()) >= 0 && date.compareTo(endDate.get()) <= 0) {
                    total += ((Number) entry.getValue()).longValue();
                }
            }
            result.put("donationsByDateRange", total);
        }

        // If username is present, include user-specific stats
        if (username.isPresent()) {
            double userTotalDonations = getTotalDonationsForUser(username.get());
            long userTotalProjects = getTotalProjectsForUser(username.get());
            result.put("userTotalDonations", userTotalDonations);
            result.put("userTotalProjects", userTotalProjects);
        }

        return result;
    }
}
