package by.pavvel.util;

import by.pavvel.dto.AttractionDto;
import by.pavvel.model.Attraction;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class AttractionDtoMapper implements Function<Attraction, AttractionDto> {

    @Override
    public AttractionDto apply(Attraction attraction) {
        return new AttractionDto(
                attraction.getId(),
                attraction.getTitle(),
                attraction.getCreationDate(),
                attraction.getDescription(),
                attraction.getAttractionType()
        );
    }
}
