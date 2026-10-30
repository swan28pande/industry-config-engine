package com.swanand.industryconfig.industry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Default configuration shared by every tenant in an industry. */
@Entity
public class IndustryProfile {
    @Id
    private String industry;

    private String displayName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false)
    private Map<String, Object> defaults = new HashMap<>();

    protected IndustryProfile() {}

    public IndustryProfile(String industry, String displayName, Map<String, Object> defaults) {
        this.industry = industry;
        this.displayName = displayName;
        this.defaults = new HashMap<>(defaults);
    }

    public String getIndustry() { return industry; }
    public String getDisplayName() { return displayName; }
    public Map<String, Object> getDefaults() { return defaults; }
    public void setDefaults(Map<String, Object> defaults) { this.defaults = new HashMap<>(defaults); }
}
