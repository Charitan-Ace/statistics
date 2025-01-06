// com/example/Statistics/service/external/ExternalStatisticsServiceInterface.java
package com.example.statistics.service.external;

import com.example.statistics.dto.external.DonationDataDTO;
import com.example.statistics.dto.external.ProjectDataDTO;

public interface ExternalStatisticsServiceInterface {
    void processDonation(DonationDataDTO donationDataDTO);
    void processProject(ProjectDataDTO projectDataDTO);
    // Add other external methods as needed
}
