package by.pavvel.dto;

import by.pavvel.model.AttractionType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AttractionDto {

    private final Long id;

    private final String title;

    private final LocalDate creationDate;

    private final String description;

    private final AttractionType attractionType;

    public AttractionDto(Long id, String title, LocalDate creationDate, String description, AttractionType attractionType) {
        this.id = id;
        this.title = title;
        this.creationDate = creationDate;
        this.description = description;
        this.attractionType = attractionType;
    }

    @Override
    public String toString() {
        return "AttractionDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", creationDate=" + creationDate +
                ", description='" + description + '\'' +
                ", attractionType=" + attractionType +
                '}';
    }
}
