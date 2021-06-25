
package main.model.apiAdresse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
//@Generated("jsonschema2pojo")

public class Geometry {

    @JsonProperty("coordinates")
    private List<Double> coordinates = new ArrayList<>();

    @JsonProperty("coordinates")
    public List<Double> getCoordinates() {
        return coordinates;
    }

    @JsonProperty("coordinates")
    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }


    @Override
    public String toString() {
        return "Geometry{" +
                "coordinates=" + coordinates +
                '}';
    }
}
