package weather.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import weather.exception.WeatherConditionException;
import weather.model.WeatherCondition;
import weather.service.WeatherConditionService;

@Service
public class WeatherConditionServiceImpl implements WeatherConditionService {

    @Value("${weather.api.key}")
    private String api_key;
    private final WebClient webClient;

    public WeatherConditionServiceImpl(@Value("${weather.api.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Cacheable("weatherCondition")
    public WeatherCondition getWeatherConditionBy(Double latitude, Double longitude) {
        return webClient.get()
                .uri("?units=metric&lat=" + latitude + "&lon=" + longitude)
                .header("x-api-key", api_key)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->
                        Mono.error(new WeatherConditionException("Client error"))
                )
                .onStatus(HttpStatus::is5xxServerError, response ->
                        Mono.error(new WeatherConditionException("Server error"))
                )
                .bodyToMono(WeatherCondition.class)
                .block();
    }
}
