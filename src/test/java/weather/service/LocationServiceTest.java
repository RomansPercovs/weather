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
import weather.model.Location;
import weather.service.impl.LocationServiceImpl;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class LocationServiceTest {

    private final MockWebServer mockWebServer = new MockWebServer();

    private final LocationService locationService = new LocationServiceImpl(mockWebServer.url("localhost/").toString());

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void shouldReturnLocation() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setBody("{\"latitude\": 100.00, \"longitude\": 100.00}")
        );

        Location response = locationService.getLocation("52.142.146.86");
        assertEquals(response.getLatitude(),response.getLatitude(), 0.000001d);
        assertEquals(response.getLongitude(),response.getLatitude(), 0.000001d);
    }

    @Test
    public void shouldReturn4xxAndThrowException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
        );

        exceptionRule.expect(LocationException.class);
        exceptionRule.expectMessage("Client error");
        locationService.getLocation("52.142.146.86");
    }

    @Test
    public void shouldReturn5xxAndThrowException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
        );

        exceptionRule.expect(LocationException.class);
        exceptionRule.expectMessage("Server error");
        locationService.getLocation("52.142.146.86");
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

}
