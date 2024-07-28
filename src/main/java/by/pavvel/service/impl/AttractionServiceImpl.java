package by.pavvel.service.impl;

import by.pavvel.dto.AttractionDto;
import by.pavvel.dto.AttractionLocalityDto;
import by.pavvel.dto.Recommendation;
import by.pavvel.exception.AttractionNotFoundException;
import by.pavvel.model.Attraction;
import by.pavvel.model.Locality;
import by.pavvel.model.weather.WeatherData;
import by.pavvel.repository.AttractionRepository;
import by.pavvel.repository.LocalityRepository;
import by.pavvel.repository.ServiceRepository;
import by.pavvel.service.AttractionService;
import by.pavvel.util.AttractionDtoMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class AttractionServiceImpl implements AttractionService {

    private static final Logger logger = LogManager.getLogger(AttractionServiceImpl.class);

    private final AttractionRepository attractionRepository;

    private final LocalityRepository localityRepository;

    private final ServiceRepository serviceRepository;

    private final AttractionDtoMapper attractionDtoMapper;

    private final RestTemplate restTemplate;

    public AttractionServiceImpl(AttractionRepository attractionRepository, LocalityRepository localityRepository, ServiceRepository serviceRepository, AttractionDtoMapper attractionDtoMapper, RestTemplate restTemplate) {
        this.attractionRepository = attractionRepository;
        this.localityRepository = localityRepository;
        this.serviceRepository = serviceRepository;
        this.attractionDtoMapper = attractionDtoMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional
    public void addAttraction(Attraction attraction, Long localityId, List<Long> servicesIds) {
        logger.info("addAttraction was called for attraction {}", attraction.getId());
        bindLocalityAndServicesToAttraction(attraction, localityId, servicesIds);
        attractionRepository.save(attraction);
    }

    private void bindLocalityAndServicesToAttraction(Attraction attraction, Long localityId, List<Long> servicesIds) {
        bindLocalityToAttraction(attraction, localityId);
        if (servicesIds != null) {
            bindServicesToAttraction(attraction, servicesIds);
        }
    }

    private void bindLocalityToAttraction(Attraction attraction, Long localityId) {
        Locality localityById = localityRepository.getReferenceById(localityId);
        attraction.setLocality(localityById);
    }

    private void bindServicesToAttraction(Attraction attraction, List<Long> servicesIds) {
        servicesIds.forEach(serviceId -> {
            by.pavvel.model.Service serviceReferenceById = serviceRepository.getReferenceById(serviceId);
            attraction.addService(serviceReferenceById);
        });
    }

    @Override
    public List<AttractionDto> getAttractions(String sortDirection, String type) {
        logger.info("getAttractions was called with params: {}, {}", sortDirection, type);
        String sortField = "title";
        Sort sort;
        if (sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())) {
            sort = Sort.by(sortField).ascending();
        } else if (sortDirection.equalsIgnoreCase(Sort.Direction.DESC.name())){
            sort = Sort.by(sortField).descending();
        } else {
            throw new IllegalStateException("Parameter not equal to asc or desc");
        }

        List<AttractionDto> attractionDtos = new ArrayList<>();
        attractionRepository.findAllSortedAttractions(type, sort)
                .forEach(attraction -> attractionDtos.add(attractionDtoMapper.apply(attraction)));
        return attractionDtos;
    }

    @Override
    public List<AttractionLocalityDto> getAttractionsByLocality(String locality) {
        logger.info("getAttractionsByLocality was called for locality {}", locality);
        return attractionRepository.findAllAttractionsByLocality(locality);
    }

    @Override
    @CachePut("weather")
    public Recommendation getWeatherInfo(Double latitude, Double longitude) {
        logger.info("getWeatherInfo was called for locality {}, {}", latitude, longitude);
        WeatherData weatherData = restTemplate.getForObject(
                "https://api.weatherapi.com/v1/current.json?key=c8d70ac73e2f4639b04132653231203&aqi=no&q={coordinates}",
                WeatherData.class,
                latitude,
                longitude
        );

        if (weatherData == null) {
            throw new IllegalStateException("Weather data is null");
        }

        String recommendation;
        Double tempC = weatherData.getCurrent().getTempC();
        Integer humidity = weatherData.getCurrent().getHumidity();
        String text = weatherData.getCurrent().getCondition().getText();
        String locality = weatherData.getLocation().getName();

        recommendation = getRecommendation(tempC, humidity);
        return new Recommendation(UUID.fromString("202ec8ce-b284-438f-a6ec-a7ef99ba3b01"), recommendation, text, locality, LocalDateTime.now());
    }

    private static String getRecommendation(Double tempC, Integer humidity) {
        String recommendation;
        if (tempC <= 0) {
            recommendation = "It's too cold. Put on a hat";
        } else if (tempC > 0 && humidity <= 80) {
            recommendation = "The weather is great. Enjoy";
        } else if (tempC > 25 && humidity > 100) {
            recommendation = "The humidity is too high. Take an umbrella";
        } else {
            recommendation = "It's too hot. Take some water";
        }
        return recommendation;
    }

    @Override
    @Transactional
    public void updateAttractionServicesAbbreviation(String serviceAbbreviation, Long attractionId) {
        logger.info("updateAttractionInfo was called for attraction {}", attractionId);
        Attraction attraction = getAttraction(attractionId);

        Set<by.pavvel.model.Service> services = attraction.getServices();
        List<Long> serviceIds = new ArrayList<>();
        services.forEach(s -> serviceIds.add(s.getId()));
        serviceIds.forEach(id -> serviceRepository.updateServiceByAbbreviation(serviceAbbreviation, id));
    }

    @Override
    @Transactional
    public void deleteAttraction(Long attractionId) {
        logger.info("deleteAttraction was called for attraction {}", attractionId);
        unbindServicesFromAttraction(attractionId);
        attractionRepository.deleteById(attractionId);
    }

    private void unbindServicesFromAttraction(Long attractionId) {
        Attraction attraction = getAttraction(attractionId);
        Set<by.pavvel.model.Service> services = attraction.getServices();
        for (by.pavvel.model.Service service : new HashSet<>(services)) {
            attraction.removeService(service);
        }
    }

    private Attraction getAttraction(Long attractionId) {
        return attractionRepository.findById(attractionId).orElseThrow(() -> {
            AttractionNotFoundException attractionNotFoundException = new AttractionNotFoundException(
                    String.format("Attraction with id %s not found", attractionId)
            );
            logger.error("error in updateAttractionInfo: {}", attractionId, attractionNotFoundException);
            return attractionNotFoundException;
        });
    }
}
