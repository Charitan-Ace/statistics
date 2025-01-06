// com.example.statistics.dto.internal.ProjectData.java
package com.example.statistics.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectData {
    private String category;
    private String continent;
    private String country;
    private String startTime;
    private String username;     // Email of the project creator (added for user-specific stats)
}
