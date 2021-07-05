package weather.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherCondition {

    double temp;

    public WeatherCondition() {
    }

    public WeatherCondition(double temp) {
        this.temp = temp;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    @JsonProperty("main")
    private void unpackNested(Map<String, Object> main) {
        this.temp = (double) main.get("temp");
    }
}
