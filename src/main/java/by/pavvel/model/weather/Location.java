package by.pavvel.model.weather;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Location {

    private String name;

    private String region;

    private String country;

    private Double lat;

    private Double lon;

    private String tzId;

    private Integer localtimeEpoch;

    private String localtime;

}