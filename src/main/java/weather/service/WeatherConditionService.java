package weather.service;

import weather.model.WeatherCondition;

public interface WeatherConditionService {
    WeatherCondition getWeatherConditionBy(Double latitude, Double longitude);
}
