package by.pavvel.service;

import by.pavvel.dto.LocalityDto;
import by.pavvel.model.Locality;

public interface LocalityService {

    void addLocality(Locality locality);

    void updateLocalityInfo(LocalityDto localityDto, Long localityId);
}
