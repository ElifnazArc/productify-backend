package dev.elifnaz.productify.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class GoldPriceService {

    private static final double FALLBACK_GOLD_PRICE_PER_GRAM = 124.0;
    private static final long CACHE_DURATION_MS = 1 * 60 * 1000; // 1 minutes
    private static final String PRICE_KEY = "price_gram_24k";

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gold.api.url}")
    private String goldApiUrl;

    @Value("${gold.api.token}")
    private String goldApiToken;

    private Double cachedGoldPrice;
    private Long cacheTimestamp;

    public double getGoldPricePerGram() {
        if (isCacheValid()) {
            log.debug("Returning cached gold price: ${}", cachedGoldPrice);
            return cachedGoldPrice;
        }

        try {
            log.info("Fetching gold price from API");
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-access-token", goldApiToken);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.exchange(
                    goldApiUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class).getBody();

            if (response != null && response.containsKey(PRICE_KEY)) {
                double goldPrice = ((Number) response.get(PRICE_KEY)).doubleValue();
                cachedGoldPrice = goldPrice;
                cacheTimestamp = System.currentTimeMillis();
                log.info("Gold price updated: ${} per gram", goldPrice);
                return goldPrice;
            }

            log.warn("Invalid response from gold API, using fallback price");
            return FALLBACK_GOLD_PRICE_PER_GRAM;

        } catch (Exception e) {
            log.error("Error fetching gold price: {}", e.getMessage());
            return cachedGoldPrice != null ? cachedGoldPrice : FALLBACK_GOLD_PRICE_PER_GRAM;
        }
    }

    private boolean isCacheValid() {
        return cachedGoldPrice != null &&
                cacheTimestamp != null &&
                (System.currentTimeMillis() - cacheTimestamp) < CACHE_DURATION_MS;
    }
}
