package weather.service;

import weather.model.Location;

public interface LocationService {
    Location getLocation(String ip);
}
