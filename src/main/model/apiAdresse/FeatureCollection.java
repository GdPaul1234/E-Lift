package main.model.apiAdresse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.model.Adresse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
//@Generated("jsonschema2pojo") (https://www.jsonschema2pojo.org/)
public class FeatureCollection {

    @JsonProperty("features")
    private List<Feature> features;
    @JsonProperty("filters")
    private Filters filters;

    @JsonProperty("features")
    public List<Feature> getFeatures() {
        return features;
    }

    @JsonProperty("features")
    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    @JsonProperty("filters")
    public Filters getFilters() {
        return filters;
    }

    @JsonProperty("filters")
    public void setFilters(Filters filters) {
        this.filters = filters;
    }

    public FeatureCollection(String query) throws URISyntaxException, IOException {
        String params = new StringBuilder()
                .append("q=")
                .append(query)
                .append("&type=housenumber")
                .append("&autocomplete=1")
                .toString();

        URI uri = new URI("https", "api-adresse.data.gouv.fr", "/search", params, null);

        System.out.println(uri);

        // https://www.baeldung.com/jackson-deserialize-json-unknown-properties
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        FeatureCollection featureCollection = mapper.readValue(new URL(uri.toString()), FeatureCollection.class);

        // Copy the returned feature collection (list of adresses returned by the gouv.fr) here
        filters = new Filters();
        filters.setType(featureCollection.filters.getType());

        features = new ArrayList<>(featureCollection.features);
    }

    public List<Adresse> toAdresseList() {
        return features.stream().map(Feature::toAdresse).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "FeatureCollection{" +
                ", features=" + features +
                ", filters=" + filters +
                '}';
    }
}
