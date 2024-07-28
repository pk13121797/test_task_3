package by.pavvel.service;

import by.pavvel.dto.AttractionDto;
import by.pavvel.dto.AttractionLocalityDto;
import by.pavvel.dto.Recommendation;
import by.pavvel.model.Attraction;

import java.util.List;

public interface AttractionService {

    void addAttraction(Attraction attraction, Long localityId, List<Long> servicesIds);

    List<AttractionDto> getAttractions(String sortDirection, String type);

    List<AttractionLocalityDto> getAttractionsByLocality(String locality);

    Recommendation getWeatherInfo(Double latitude, Double longitude);

    void updateAttractionServicesAbbreviation(String serviceAbbreviation, Long attractionId);

    void deleteAttraction(Long attractionId);
}
