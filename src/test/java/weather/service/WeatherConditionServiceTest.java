package weather.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import weather.exception.LocationException;
import weather.exception.WeatherConditionException;
import weather.model.WeatherCondition;
import weather.service.impl.WeatherConditionServiceImpl;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class WeatherConditionServiceTest {

    private final MockWebServer mockWebServer = new MockWebServer();

    private final WeatherConditionService weatherConditionService = new WeatherConditionServiceImpl(mockWebServer.url("localhost/").toString());

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void shouldReturnWeatherCondition() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\"main\": {\"temp\": 12.23}}")
        );

        WeatherCondition response = weatherConditionService.getWeatherConditionBy(100.00, 100.00);
        assertEquals(12.23, response.getTemp(), 0.000001d);
    }

    @Test
    public void shouldReturn4xxAndThrowException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
        );

        exceptionRule.expect(WeatherConditionException.class);
        exceptionRule.expectMessage("Client error");
        weatherConditionService.getWeatherConditionBy(100.00, 100.00);
    }

    @Test
    public void shouldReturn5xxAndThrowException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
        );

        exceptionRule.expect(WeatherConditionException.class);
        exceptionRule.expectMessage("Server error");
        weatherConditionService.getWeatherConditionBy(100.00, 100.00);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

}
