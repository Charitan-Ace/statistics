package com.example.Statistics.Controller;

import com.example.Statistics.Service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/donations/category")
    public Map<Object, Object> getDonationsByCategory() {
        return statisticsService.getTotalDonationsByCategory();
    }

    @GetMapping("/projects/category")
    public Map<Object, Object> getProjectsByCategory() {
        return statisticsService.getTotalProjectsByCategory();
    }

    @GetMapping("/donations/continent")
    public Map<Object, Object> getDonationsByContinent() {
        return statisticsService.getTotalDonationsByContinent();
    }

    @GetMapping("/projects/continent")
    public Map<Object, Object> getProjectsByContinent() {
        return statisticsService.getTotalProjectsByContinent();
    }
}

