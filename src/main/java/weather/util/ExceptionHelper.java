package weather.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import weather.exception.LocationException;
import weather.exception.WeatherConditionException;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@ControllerAdvice
public class ExceptionHelper {

    @ExceptionHandler(LocationException.class)
    public ResponseEntity handleLocationException(LocationException e) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("Location service error: " + e.getMessage());
    }

    @ExceptionHandler(WeatherConditionException.class)
    public ResponseEntity handleWeatherConditionException(WeatherConditionException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Weather condition service error: " + e.getMessage());
    }

}
