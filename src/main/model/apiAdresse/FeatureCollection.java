package main.model.apiAdresse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.model.Adresse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@JsonInclude(JsonInclude.Include.NON_NULL)
//@Generated("jsonschema2pojo") (https://www.jsonschema2pojo.org/)
public class FeatureCollection {

    @JsonProperty("features")
    private List<Feature> features;
    @JsonProperty("filters")
    private Filters filters;

    public FeatureCollection() {

    }

    public FeatureCollection(String query) throws URISyntaxException {

        // https://www.baeldung.com/java-url-encoding-decoding
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("q", query);
        requestParams.put("type", "housenumber");
        requestParams.put("autocomplete", "1");

        String params = requestParams.keySet().stream()
                .map(key -> {
                    String param = "";
                    try {
                        param = key + "=" + URLEncoder.encode(requestParams.get(key), StandardCharsets.UTF_8.toString());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return param;
                })
                .collect(joining("&"));

        URI uri = new URI("https", "api-adresse.data.gouv.fr", "/search", params, null);

        // https://www.baeldung.com/jackson-deserialize-json-unknown-properties
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            FeatureCollection featureCollection = mapper.readValue(new URL(uri.toString()), FeatureCollection.class);

            // Copy the returned feature collection (list of adresses returned by the gouv.fr) here
            filters = new Filters();
            if(featureCollection.filters != null) {
                filters.setType(featureCollection.filters.getType());
                features = new ArrayList<>(featureCollection.features);
            } else {
                features = new ArrayList<>();
            }

        } catch (IOException e) {
            features = new ArrayList<>();
        }

    }

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
