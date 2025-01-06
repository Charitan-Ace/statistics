// com/example/Statistics/utils/CacheInitializer.java
package com.example.statistics.utils;

import com.example.statistics.service.internal.StatisticsServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CacheInitializer implements CommandLineRunner {

    @Autowired
    private StatisticsServiceInterface statisticsService;

    @Override
    public void run(String... args) {
        System.out.println("Checking Redis cache...");

        boolean isCacheEmpty = statisticsService.getTotalDonationsByCategory().isEmpty() ||
                statisticsService.getTotalProjectsByCategory().isEmpty();

        if (isCacheEmpty) {
            System.out.println("Redis cache is empty. Recalculating statistics...");
            statisticsService.recalculateStatistics();
        } else {
            System.out.println("Redis cache is already populated.");
        }
    }
}
