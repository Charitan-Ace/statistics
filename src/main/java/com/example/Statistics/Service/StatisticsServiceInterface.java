package com.example.Statistics.Service;

import com.example.Statistics.DTO.StatisticsDto;

public interface StatisticsServiceInterface {

    String createProject(StatisticsDto statisticsDto);

    StatisticsDto getProjectById(String id);

    String updateProject(String id, StatisticsDto statisticsDto);

    String deleteProject(String id);
}
