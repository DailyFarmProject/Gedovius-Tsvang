package dailyfarm.accounting.service.geocoding;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GeocodingService {

    @Value("${google.api.key}")
    private String apiKey;

    private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s";

    public Point getCoordinatesFromAddress(String address) {
        String url = String.format(GEOCODE_URL, address, apiKey);
        RestTemplate restTemplate = new RestTemplate();
        GeocodeResponse response = restTemplate.getForObject(url, GeocodeResponse.class);

        if (response != null && !response.getResults().isEmpty()) {
            double latitude = response.getResults().get(0).getGeometry().getLocation().getLat();
            double longitude = response.getResults().get(0).getGeometry().getLocation().getLng();

            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
            Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
            point.setSRID(4326);
            return point;
  
        }

        return null;
    }
}
