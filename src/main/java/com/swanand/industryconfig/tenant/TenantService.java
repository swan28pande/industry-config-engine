package com.swanand.industryconfig.tenant;

import com.swanand.industryconfig.classifier.ClassifierClient;
import com.swanand.industryconfig.industry.IndustryProfile;
import com.swanand.industryconfig.industry.IndustryProfileRepository;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TenantService {
    private final TenantRepository tenants;
    private final IndustryProfileRepository industries;
    private final ClassifierClient classifier;

    public TenantService(TenantRepository tenants, IndustryProfileRepository industries, ClassifierClient classifier) {
        this.tenants = tenants;
        this.industries = industries;
        this.classifier = classifier;
    }

    @Transactional
    public Tenant create(String name, String description, String industry) {
        Tenant t = new Tenant(name, description);
        if (industry != null && !industry.isBlank()) {
            requireIndustry(industry);
            t.setIndustry(industry, "manual", null);
        } else {
            var result = classifier.classify(description == null ? name : name + ". " + description);
            String detected = industries.existsById(result.industry()) ? result.industry() : "other";
            t.setIndustry(detected, "classifier", result.confidence());
        }
        return tenants.save(t);
    }

    public Tenant get(Long id) {
        return tenants.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "tenant not found"));
    }

    /** Industry defaults with the tenant's overrides merged on top (deep merge for nested maps). */
    public Map<String, Object> effectiveConfig(Long id) {
        Tenant t = get(id);
        IndustryProfile p = requireIndustry(t.getIndustry());
        return merge(p.getDefaults(), t.getOverrides());
    }

    @Transactional
    public Tenant updateOverrides(Long id, Map<String, Object> overrides) {
        Tenant t = get(id);
        t.setOverrides(overrides);
        return tenants.save(t);
    }

    @Transactional
    public Tenant setIndustry(Long id, String industry) {
        Tenant t = get(id);
        requireIndustry(industry);
        t.setIndustry(industry, "manual", null);
        return tenants.save(t);
    }

    private IndustryProfile requireIndustry(String industry) {
        return industries.findById(industry)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "unknown industry: " + industry));
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> merge(Map<String, Object> base, Map<String, Object> over) {
        Map<String, Object> out = new HashMap<>(base);
        over.forEach((k, v) -> {
            if (v instanceof Map && out.get(k) instanceof Map) {
                out.put(k, merge((Map<String, Object>) out.get(k), (Map<String, Object>) v));
            } else {
                out.put(k, v);
            }
        });
        return out;
    }
}
