// com.example.statistics.controller.StatisticsController.java
package com.example.statistics.controller;

import com.example.statistics.service.internal.StatisticsServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsServiceInterface statisticsService;

    /**
     * Admins can retrieve all donations by category.
     * Users can retrieve only their own donations by category.
     */
    @GetMapping("/donations/category")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Map<Object, Object> getDonationsByCategory(Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return statisticsService.getTotalDonationsByCategory();
        } else {
            String username = authentication.getName();
            return statisticsService.getDonationsByCategoryForUser(username);
        }
    }

    /**
     * Admins can retrieve all projects by category.
     * Users can retrieve only their own projects by category.
     */
    @GetMapping("/projects/category")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Map<Object, Object> getProjectsByCategory(Authentication authentication) {
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return statisticsService.getTotalProjectsByCategory();
        } else {
            String username = authentication.getName();
            return statisticsService.getProjectsByCategoryForUser(username);
        }
    }

    /**
     * Retrieve total donations and total projects.
     * If 'username' is specified, admin can retrieve totals for that user.
     * Users can retrieve their own totals.
     */
    @GetMapping("/totals")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Map<String, Object> getTotals(@RequestParam(required = false) String username, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String authenticatedUsername = authentication.getName();

        if (username != null && !username.isEmpty()) {
            if (!isAdmin) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins can specify a username to retrieve user-specific totals.");
            }
            // Admin retrieving totals for specified user
            double userTotalDonations = statisticsService.getTotalDonationsForUser(username);
            long userTotalProjects = statisticsService.getTotalProjectsForUser(username);
            return Map.of(
                    "userTotalDonations", userTotalDonations,
                    "userTotalProjects", userTotalProjects
            );
        } else {
            // Retrieve global totals
            double globalTotalDonations = statisticsService.getTotalDonations();
            long globalTotalProjects = statisticsService.getTotalProjects();

            if (isAdmin) {
                return Map.of(
                        "totalDonations", globalTotalDonations,
                        "totalProjects", globalTotalProjects
                );
            } else {
                // For regular user, also include their own totals
                double userTotalDonations = statisticsService.getTotalDonationsForUser(authenticatedUsername);
                long userTotalProjects = statisticsService.getTotalProjectsForUser(authenticatedUsername);

                return Map.of(
                        "totalDonations", globalTotalDonations,
                        "totalProjects", globalTotalProjects,
                        "userTotalDonations", userTotalDonations,
                        "userTotalProjects", userTotalProjects
                );
            }
        }
    }

    /**
     * Admins can retrieve statistics for any user.
     * Users can retrieve only their own statistics.
     */
    @GetMapping("/user/{username}/stats")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public Map<String, Object> getUserStats(@PathVariable String username, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !username.equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Users can only retrieve their own statistics.");
        }

        double userTotalDonations = statisticsService.getTotalDonationsForUser(username);
        long userTotalProjects = statisticsService.getTotalProjectsForUser(username);

        // Optionally, include other user-specific stats
        // e.g., donations by category, projects by category, etc.

        return Map.of(
                "userTotalDonations", userTotalDonations,
                "userTotalProjects", userTotalProjects
        );
    }

    /**
     * Retrieve filtered statistics.
     * Admins can filter across all users.
     * Users can filter only their own statistics.
     */
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public Map<String, Object> getFilteredStatistics(
            @RequestParam Optional<String> continent,
            @RequestParam Optional<String> country,
            @RequestParam Optional<String> category,
            @RequestParam Optional<String> startDate,
            @RequestParam Optional<String> endDate,
            Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        Optional<String> username = isAdmin ? Optional.empty() : Optional.of(authentication.getName());

        return statisticsService.getFilteredStatistics(continent, country, category, startDate, endDate, username);
    }
}
