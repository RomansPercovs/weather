package weather.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import weather.exception.LocationException;
import weather.model.Location;
import weather.service.LocationService;

@Service
public class LocationServiceImpl implements LocationService {

    @Value("${location.api.key}")
    private String api_key;
    @Value("${location.api.host}")
    private String api_host;

    private final WebClient webClient;

    public LocationServiceImpl(@Value("${location.api.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Cacheable("location")
    public Location getLocation(String ip) {
        return webClient.get()
                .uri("?objects=latitude,longitude&ip=" + ip)
                .header("x-rapidapi-key", api_key)
                .header("x-rapidapi-host", api_host)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->
                        Mono.error(new LocationException("Client error"))
                )
                .onStatus(HttpStatus::is5xxServerError, response ->
                        Mono.error(new LocationException("Server error"))
                )
                .bodyToMono(Location.class)
                .block();
    }
}
