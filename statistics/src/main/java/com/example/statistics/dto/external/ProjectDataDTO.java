// com.example.statistics.dto.external.ProjectDataDTO.java
package com.example.statistics.dto.external;

import lombok.Data;

@Data
public class ProjectDataDTO {
    private String category;
    private String continent;
    private String country;
    private String startTime;    // (ISO 8601 format)
    private String username;     // Email of the project creator (added for user-specific stats)
}
