package com.swanand.industryconfig.industry;

import java.util.List;
import java.util.Map;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Inserts starter industry profiles on first boot. */
@Component
public class IndustrySeeder implements CommandLineRunner {
    private final IndustryProfileRepository repo;

    public IndustrySeeder(IndustryProfileRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        List.of(
            new IndustryProfile("healthcare", "Healthcare", Map.of(
                "compliance", List.of("HIPAA"), "dataRetentionDays", 2555, "auditLog", true,
                "features", Map.of("patientPortal", true, "billing", true))),
            new IndustryProfile("finance", "Financial Services", Map.of(
                "compliance", List.of("SOC2", "PCI-DSS"), "dataRetentionDays", 2555, "auditLog", true,
                "features", Map.of("kyc", true, "fraudAlerts", true))),
            new IndustryProfile("retail", "Retail & E-commerce", Map.of(
                "compliance", List.of("PCI-DSS"), "dataRetentionDays", 730, "auditLog", false,
                "features", Map.of("inventory", true, "loyalty", true))),
            new IndustryProfile("education", "Education", Map.of(
                "compliance", List.of("FERPA"), "dataRetentionDays", 1825, "auditLog", true,
                "features", Map.of("gradebook", true, "parentAccess", true))),
            new IndustryProfile("manufacturing", "Manufacturing", Map.of(
                "compliance", List.of("ISO-9001"), "dataRetentionDays", 1095, "auditLog", true,
                "features", Map.of("supplyChain", true, "qualityControl", true))),
            new IndustryProfile("other", "General", Map.of(
                "compliance", List.of(), "dataRetentionDays", 365, "auditLog", false,
                "features", Map.of()))
        ).forEach(p -> {
            if (!repo.existsById(p.getIndustry())) repo.save(p);
        });
    }
}
