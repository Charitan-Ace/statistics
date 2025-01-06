// com.example.statistics.service.internal.StatisticsServiceInterface.java
package com.example.statistics.service.internal;

import java.util.Map;
import java.util.Optional;

public interface StatisticsServiceInterface {

    void recalculateStatistics();
    void updateDonationStats(String donor, double amount, String category, String continent, String country, String timestamp);
    void updateProjectStats(String category, String continent, String country, String startTime, String username);

    // Existing methods
    Map<Object, Object> getTotalDonationsByCategory();
    Map<Object, Object> getTotalProjectsByCategory();
    Map<Object, Object> getTotalDonationsByContinent();
    Map<Object, Object> getTotalProjectsByContinent();
    Map<Object, Object> getTotalDonationsByCountry();
    Map<Object, Object> getTotalProjectsByCountry();
    Map<Object, Object> getTotalDonationsByDate();
    Map<Object, Object> getTotalProjectsByDate();

    // New methods for user-specific statistics
    Map<Object, Object> getDonationsByCategoryForUser(String username);
    Map<Object, Object> getProjectsByCategoryForUser(String username);

    // New methods for total donations and total projects using SCAN
    double getTotalDonations();
    long getTotalProjects();

    double getTotalDonationsForUser(String username);
    long getTotalProjectsForUser(String username);

    // Adjusted filtered statistics method
    Map<String, Object> getFilteredStatistics(Optional<String> continent,
                                              Optional<String> country,
                                              Optional<String> category,
                                              Optional<String> startDate,
                                              Optional<String> endDate,
                                              Optional<String> username);
}
