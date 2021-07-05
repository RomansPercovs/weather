package weather.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import weather.model.Location;
import weather.service.LocationService;
import weather.service.WeatherConditionService;

import javax.servlet.http.HttpServletRequest;

@RestController
public class WeatherController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private WeatherConditionService weatherConditionService;

    @GetMapping("/weather")
    public ResponseEntity getWeather(HttpServletRequest request) {
        String ip = retrieveClientIp(request);
        Location location = locationService.getLocation(ip);
        return ResponseEntity.status(HttpStatus.OK).body(weatherConditionService.getWeatherConditionBy(location.getLatitude(), location.getLongitude()));
    }

    private String retrieveClientIp(HttpServletRequest request) {
        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr.contains(",") ? remoteAddr.split(",")[0] : remoteAddr;
    }
}
