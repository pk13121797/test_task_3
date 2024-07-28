package by.pavvel.model.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Current {

    private Integer lastUpdatedEpoch;

    private String lastUpdated;

    @JsonProperty("temp_c")
    private Double tempC;

    private Double tempF;

    private Integer isDay;

    private Condition condition;

    private Double windMph;

    private Double windKph;

    private Integer windDegree;

    private String windDir;

    private Double pressureMb;

    private Double pressureIn;

    private Double precipMm;

    private Double precipIn;

    private Integer humidity;

    private Integer cloud;

    private Double feelslikeC;

    private Double feelslikeF;

    private Double visKm;

    private Double visMiles;

    private Double uv;

    private Double gustMph;

    private Double gustKph;
}