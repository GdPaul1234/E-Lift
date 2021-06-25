package main.model.apiAdresse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import main.model.Adresse;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "type",
        "geometry",
        "properties"
})
//@Generated("jsonschema2pojo")
public class Feature {

    @JsonProperty("type")
    private String type;
    @JsonProperty("geometry")
    private Geometry geometry;
    @JsonProperty("properties")
    private Properties properties;

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("geometry")
    public Geometry getGeometry() {
        return geometry;
    }

    @JsonProperty("geometry")
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    @JsonProperty("properties")
    public Properties getProperties() {
        return properties;
    }

    @JsonProperty("properties")
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Convert feature to adresse
     * @return converted adresse
     */
    public Adresse toAdresse() {
        Double latitude = geometry.getCoordinates().get(1);
        Double longitude = geometry.getCoordinates().get(0);
        return new Adresse(properties.getName(), properties.getCity(), properties.getPostcode(),
                latitude.floatValue(), longitude.floatValue());
    }

    @Override
    public String toString() {
        return "Feature{" +
                "type='" + type + '\'' +
                ", geometry=" + geometry +
                ", properties=" + properties +
                '}';
    }
}
