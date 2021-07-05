package weather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import weather.exception.LocationException;
import weather.exception.WeatherConditionException;
import weather.model.Location;
import weather.model.WeatherCondition;
import weather.service.LocationService;
import weather.service.WeatherConditionService;
import weather.service.impl.LocationServiceImpl;
import weather.service.impl.WeatherConditionServiceImpl;
import weather.util.ExceptionHelper;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(WeatherController.class)
public class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    @MockBean
    private WeatherConditionService weatherConditionService;

    @Autowired
    private WeatherController weatherController;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(weatherController).setControllerAdvice(new ExceptionHelper()).build();
    }

    @Test
    public void getWeather() throws Exception {
        Location location = new Location(100.00, 100.00);
        WeatherCondition expectedCondition = new WeatherCondition(12.34);
        when(locationService.getLocation("1.2.3.4")).thenReturn(location);
        when(weatherConditionService.getWeatherConditionBy(location.getLatitude(), location.getLongitude()))
                .thenReturn(expectedCondition);
        MvcResult result = mockMvc.perform(get("/weather").header("X-FORWARDED-FOR", "1.2.3.4"))
                .andExpect(status().isOk())
                .andReturn();
        WeatherCondition actualCondition = new ObjectMapper().readValue(result.getResponse().getContentAsString(), WeatherCondition.class);
        assertEquals(expectedCondition.getTemp(), actualCondition.getTemp(), 0.000001d);
        verify(locationService).getLocation("1.2.3.4");
        verify(weatherConditionService).getWeatherConditionBy(100.00, 100.00);
    }

    @Test
    public void getWeather_shouldThrowLocationException() throws Exception {
        when(locationService.getLocation("0.0.0.0")).thenThrow(new LocationException("Client error"));
        MvcResult result = mockMvc.perform(get("/weather").header("X-FORWARDED-FOR", "0.0.0.0"))
                .andExpect(status().isInternalServerError())
                .andReturn();
        assertEquals("Location service error: Client error", result.getResponse().getContentAsString());
        verify(locationService).getLocation("0.0.0.0");
        verify(weatherConditionService, never()).getWeatherConditionBy(any(),any());
    }

    @Test
    public void getWeather_shouldThrowWeatherConditionException() throws Exception {
        Location location = new Location(100.00, 100.00);
        when(locationService.getLocation("1.2.3.4")).thenReturn(location);
        when(weatherConditionService.getWeatherConditionBy(100.00,100.00)).thenThrow(new WeatherConditionException("Server error"));
        MvcResult result = mockMvc.perform(get("/weather").header("X-FORWARDED-FOR", "1.2.3.4"))
                .andExpect(status().isInternalServerError())
                .andReturn();
        assertEquals("Weather condition service error: Server error", result.getResponse().getContentAsString());
        verify(locationService).getLocation("1.2.3.4");
        verify(weatherConditionService).getWeatherConditionBy(100.00,100.00);
    }
}
