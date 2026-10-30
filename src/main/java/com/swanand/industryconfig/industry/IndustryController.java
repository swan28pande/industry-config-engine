package com.swanand.industryconfig.industry;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/industries")
public class IndustryController {
    private final IndustryProfileRepository repo;

    public IndustryController(IndustryProfileRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<IndustryProfile> list() {
        return repo.findAll();
    }

    @GetMapping("/{industry}")
    public IndustryProfile get(@PathVariable String industry) {
        return repo.findById(industry)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "unknown industry"));
    }

    @PutMapping("/{industry}/defaults")
    public IndustryProfile updateDefaults(@PathVariable String industry, @RequestBody Map<String, Object> defaults) {
        IndustryProfile p = get(industry);
        p.setDefaults(defaults);
        return repo.save(p);
    }
}
