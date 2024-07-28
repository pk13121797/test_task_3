package by.pavvel.model.weather;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherData {

    private Location location;

    private Current current;
}
