package com.swanand.industryconfig.tenant;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String industry;

    /** How the industry was set: "manual" or "classifier". */
    private String industrySource;

    private Double industryConfidence;

    /** Tenant-specific settings layered on top of the industry defaults. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private Map<String, Object> overrides = new HashMap<>();

    private Instant createdAt = Instant.now();

    protected Tenant() {}

    public Tenant(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIndustry() { return industry; }
    public String getIndustrySource() { return industrySource; }
    public Double getIndustryConfidence() { return industryConfidence; }
    public Map<String, Object> getOverrides() { return overrides; }
    public Instant getCreatedAt() { return createdAt; }

    public void setIndustry(String industry, String source, Double confidence) {
        this.industry = industry;
        this.industrySource = source;
        this.industryConfidence = confidence;
    }

    public void setOverrides(Map<String, Object> overrides) { this.overrides = new HashMap<>(overrides); }
}
