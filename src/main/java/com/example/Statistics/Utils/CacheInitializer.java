package com.example.Statistics.Utils;

import com.example.Statistics.Service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CacheInitializer implements CommandLineRunner {

    @Autowired
    private StatisticsService statisticsService;

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
