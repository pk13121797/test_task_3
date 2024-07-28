package by.pavvel.service.impl;

import by.pavvel.dto.LocalityDto;
import by.pavvel.exception.LocalityNotFoundException;
import by.pavvel.model.Locality;
import by.pavvel.repository.LocalityRepository;
import by.pavvel.service.LocalityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LocalityServiceImpl implements LocalityService {

    private static final Logger logger = LogManager.getLogger(LocalityServiceImpl.class);

    private final LocalityRepository localityRepository;

    public LocalityServiceImpl(LocalityRepository localityRepository) {
        this.localityRepository = localityRepository;
    }

    @Override
    public void addLocality(Locality locality) {
        logger.info("addLocality was called for locality {}", locality);
        localityRepository.save(locality);
    }

    @Override
    public void updateLocalityInfo(LocalityDto localityDto, Long localityId) {
        logger.info("updateLocalityInfo was called for attraction {}", localityId);
        localityRepository.findById(localityId).orElseThrow(() -> {
            LocalityNotFoundException localityNotFoundException = new LocalityNotFoundException(
                    String.format("Locality with id %s not found", localityId)
            );
            logger.error("error in updateLocalityInfo: {}", localityId, localityNotFoundException);
            return localityNotFoundException;
        });
        localityRepository.updateLocality(localityDto.getPopulation(), localityDto.getMetroAvailability(), localityId);
    }
}
