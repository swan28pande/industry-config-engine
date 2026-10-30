package com.swanand.industryconfig.classifier;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** Calls the Python classifier service; falls back to "other" if it is down or unsure. */
@Component
public class ClassifierClient {
    private static final Logger log = LoggerFactory.getLogger(ClassifierClient.class);

    public record Result(String industry, double confidence) {}

    private final RestClient http;
    private final double minConfidence;

    public ClassifierClient(@Value("${classifier.url}") String url,
                            @Value("${classifier.min-confidence}") double minConfidence) {
        this.http = RestClient.builder().baseUrl(url).build();
        this.minConfidence = minConfidence;
    }

    public Result classify(String text) {
        try {
            Result r = http.post().uri("/classify").body(Map.of("text", text)).retrieve().body(Result.class);
            if (r == null || r.confidence() < minConfidence) {
                return new Result("other", r == null ? 0 : r.confidence());
            }
            return r;
        } catch (Exception e) {
            log.warn("classifier unavailable, defaulting to 'other': {}", e.getMessage());
            return new Result("other", 0);
        }
    }
}
