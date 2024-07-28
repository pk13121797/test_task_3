package by.pavvel.dto;

import by.pavvel.model.AttractionType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AttractionLocalityDto {

    private final Long id;

    private final String title;

    private final LocalDate creationDate;

    private final String description;

    private final AttractionType attractionType;

    private final Double population;

    private final Boolean metroAvailability;

    public AttractionLocalityDto(Long id, String title, LocalDate creationDate, String description, AttractionType attractionType, Double population, Boolean metroAvailability) {
        this.id = id;
        this.title = title;
        this.creationDate = creationDate;
        this.description = description;
        this.attractionType = attractionType;
        this.population = population;
        this.metroAvailability = metroAvailability;
    }

    @Override
    public String toString() {
        return "AttractionLocalityDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", creationDate=" + creationDate +
                ", description='" + description + '\'' +
                ", attractionType=" + attractionType +
                ", population=" + population +
                ", metroAvailability=" + metroAvailability +
                '}';
    }
}
