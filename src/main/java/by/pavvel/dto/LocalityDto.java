package by.pavvel.dto;

import lombok.Getter;

@Getter
public class LocalityDto {

    private Double population;

    private Boolean metroAvailability;

    public LocalityDto() {
    }

    public LocalityDto(Double population, Boolean metroAvailability) {
        this.population = population;
        this.metroAvailability = metroAvailability;
    }

    @Override
    public String toString() {
        return "LocalityDto{" +
                "population=" + population +
                ", metroAvailability=" + metroAvailability +
                '}';
    }
}
