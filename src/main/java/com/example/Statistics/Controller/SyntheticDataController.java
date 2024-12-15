package com.example.Statistics.Controller;

import com.example.Statistics.Utils.SyntheticMessageProducer;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/synthetic")
public class SyntheticDataController {

    private final SyntheticMessageProducer producer;

    public SyntheticDataController(SyntheticMessageProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/donations")
    public String generateDonationMessages(@RequestParam int count) {
        producer.sendDonationMessages(count);
        return count + " donation messages sent to Kafka.";
    }

    @PostMapping("/projects")
    public String generateProjectMessages(@RequestParam int count) {
        producer.sendProjectMessages(count);
        return count + " project messages sent to Kafka.";
    }
}
