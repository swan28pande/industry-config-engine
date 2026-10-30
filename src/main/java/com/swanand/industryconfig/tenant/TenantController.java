package com.swanand.industryconfig.tenant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {
    private final TenantService service;

    public TenantController(TenantService service) {
        this.service = service;
    }

    /** If industry is omitted, the AI classifier detects it from the name and description. */
    public record CreateTenant(@NotBlank String name, String description, String industry) {}

    public record SetIndustry(@NotBlank String industry) {}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tenant create(@Valid @RequestBody CreateTenant req) {
        return service.create(req.name(), req.description(), req.industry());
    }

    @GetMapping("/{id}")
    public Tenant get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}/industry")
    public Tenant setIndustry(@PathVariable Long id, @Valid @RequestBody SetIndustry req) {
        return service.setIndustry(id, req.industry());
    }

    @GetMapping("/{id}/config")
    public Map<String, Object> config(@PathVariable Long id) {
        return service.effectiveConfig(id);
    }

    @PutMapping("/{id}/config")
    public Tenant updateOverrides(@PathVariable Long id, @RequestBody Map<String, Object> overrides) {
        return service.updateOverrides(id, overrides);
    }
}
