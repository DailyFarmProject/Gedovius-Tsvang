package dailyfarm.accounting.service.geocoding;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class GeocodeResponse {

    @JsonProperty("results")
    private List<GeocodeResult> results;

    @Getter
    @Setter
    public static class GeocodeResult {
        @JsonProperty("geometry")
        private Geometry geometry;

        @Getter
        @Setter
        public static class Geometry {
            @JsonProperty("location")
            private Location location;

            @Getter
            @Setter
            public static class Location {
                @JsonProperty("lat")
                private double lat;

                @JsonProperty("lng")
                private double lng;
            }
        }
    }
}
