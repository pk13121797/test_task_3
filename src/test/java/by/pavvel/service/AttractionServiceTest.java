package by.pavvel.service;

import by.pavvel.dto.AttractionDto;
import by.pavvel.dto.AttractionLocalityDto;
import by.pavvel.dto.Recommendation;
import by.pavvel.exception.AttractionNotFoundException;
import by.pavvel.model.Attraction;
import by.pavvel.model.AttractionType;
import by.pavvel.model.Locality;
import by.pavvel.model.Service;
import by.pavvel.model.weather.Condition;
import by.pavvel.model.weather.Current;
import by.pavvel.model.weather.Location;
import by.pavvel.model.weather.WeatherData;
import by.pavvel.repository.AttractionRepository;
import by.pavvel.repository.LocalityRepository;
import by.pavvel.repository.ServiceRepository;
import by.pavvel.service.impl.AttractionServiceImpl;
import by.pavvel.util.AttractionDtoMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

class AttractionServiceTest {

    @Mock
    private AttractionRepository attractionRepository;

    @Mock
    private LocalityRepository localityRepository;

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private AttractionDtoMapper attractionDtoMapper;

    @Mock
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<Attraction> attractionArgumentCaptor;

    private AutoCloseable autoCloseable;

    private AttractionService underTest;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new AttractionServiceImpl(
                attractionRepository,
                localityRepository,
                serviceRepository,
                attractionDtoMapper,
                restTemplate
        );
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldGetAttractionsAsc() {
        // given
        Attraction attraction = new Attraction();
        String sortDirection = "asc";
        String type = "PALACE";

        when(attractionRepository.findAllSortedAttractions(type, Sort.by("title").ascending()))
                .thenReturn(List.of(attraction));

        // when
        List<AttractionDto> attractions = underTest.getAttractions(sortDirection, type);

        // then
        assertThat(attractions).isNotNull().hasSize(1);
    }

    @Test
    void shouldGetAttractionsDesc() {
        // given
        Attraction attraction = new Attraction();
        String sortDirection = "desc";
        String type = "PALACE";

        when(attractionRepository.findAllSortedAttractions(type, Sort.by("title").descending()))
                .thenReturn(List.of(attraction));

        // when
        List<AttractionDto> attractions = underTest.getAttractions(sortDirection, type);

        // then
        assertThat(attractions).isNotNull().hasSize(1);
    }


    @Test
    void shouldThrowExceptionWhenTaskNotExists() {
        // given
        String sortDirection = "fff";
        String type = "PALACE";

        // when
        // then
        assertThatThrownBy(() -> underTest.getAttractions(sortDirection, type))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Parameter not equal to asc or desc");
    }


    @Test
    void shouldGetAttractionsByLocality() {
        // given
        Long id = 10L;
        String title = "";
        LocalDate localDate = LocalDate.now();
        String description = "";
        AttractionType attractionType = AttractionType.PALACE;
        Double population = 2000000.0;
        boolean metroAvailability = true;

        String locality = "Minsk";
        AttractionLocalityDto attractionLocalityDto = new AttractionLocalityDto(
                id,
                title,
                localDate,
                description,
                attractionType,
                population,
                metroAvailability
        );
        when(attractionRepository.findAllAttractionsByLocality(locality)).thenReturn(List.of(attractionLocalityDto));

        // when
        List<AttractionLocalityDto> attractionsByLocality = underTest.getAttractionsByLocality(locality);

        // then
        assertThat(attractionsByLocality)
                .isNotNull()
                .contains(attractionLocalityDto)
                .hasSize(1);
    }

    @Test
    void shouldGetWeatherInfoWhenTemperatureLessThan0() {
        // given
        Double latitude = 66.766237;
        Double longitude = 33.632615;

        WeatherData weatherData = new WeatherData();
        Current current = new Current();
        current.setCondition(new Condition("Sunny", "//cdn.weatherapi.com/weather/64x64/day/113.png", 1000));
        current.setTempC(-5.0);

        weatherData.setCurrent(current);
        Location location = new Location();
        location.setName("Porya");
        weatherData.setLocation(location);
        when(restTemplate.getForObject(
                "https://api.weatherapi.com/v1/current.json?key=c8d70ac73e2f4639b04132653231203&aqi=no&q={coordinates}",
                WeatherData.class,
                latitude,
                longitude)
        ).thenReturn(weatherData);

        // when
        Recommendation recommendation = underTest.getWeatherInfo(latitude, longitude);

        // then
        String text = "It's too cold. Put on a hat";
        assertThat(recommendation.getRecommendation())
                .isNotNull()
                .isEqualTo(text);
    }

    @Test
    void shouldGetWeatherInfoWhenTemperatureMoreThan0AndHumidityLessThan80() {
        // given
        Double latitude = 66.766237;
        Double longitude = 33.632615;

        WeatherData weatherData = new WeatherData();
        Current current = new Current();
        current.setCondition(new Condition("Sunny", "//cdn.weatherapi.com/weather/64x64/day/113.png", 1000));
        current.setTempC(0.1);
        current.setHumidity(80);

        weatherData.setCurrent(current);
        Location location = new Location();
        location.setName("Porya");
        weatherData.setLocation(location);
        when(restTemplate.getForObject(
                "https://api.weatherapi.com/v1/current.json?key=c8d70ac73e2f4639b04132653231203&aqi=no&q={coordinates}",
                WeatherData.class,
                latitude,
                longitude)
        ).thenReturn(weatherData);

        // when
        Recommendation recommendation = underTest.getWeatherInfo(latitude, longitude);

        // then
        String text = "The weather is great. Enjoy";
        assertThat(recommendation.getRecommendation())
                .isNotNull()
                .isEqualTo(text);
    }

    @Test
    void shouldGetWeatherInfoWhenTemperatureMoreThan25AndHumidityMoreThan100() {
        // given
        Double latitude = 66.766237;
        Double longitude = 33.632615;

        WeatherData weatherData = new WeatherData();
        Current current = new Current();
        current.setCondition(new Condition("Sunny", "//cdn.weatherapi.com/weather/64x64/day/113.png", 1000));
        current.setTempC(25.1);
        current.setHumidity(101);

        weatherData.setCurrent(current);
        Location location = new Location();
        location.setName("Porya");
        weatherData.setLocation(location);
        when(restTemplate.getForObject(
                "https://api.weatherapi.com/v1/current.json?key=c8d70ac73e2f4639b04132653231203&aqi=no&q={coordinates}",
                WeatherData.class,
                latitude,
                longitude)
        ).thenReturn(weatherData);

        // when
        Recommendation recommendation = underTest.getWeatherInfo(latitude, longitude);

        // then
        String text = "The humidity is too high. Take an umbrella";
        assertThat(recommendation.getRecommendation())
                .isNotNull()
                .isEqualTo(text);
    }

    @Test
    void shouldGetDefaultWeatherInfo() {
        // given
        Double latitude = 66.766237;
        Double longitude = 33.632615;

        WeatherData weatherData = new WeatherData();
        Current current = new Current();
        current.setCondition(new Condition("Sunny", "//cdn.weatherapi.com/weather/64x64/day/113.png", 1000));
        current.setTempC(30.0);
        current.setHumidity(90);

        weatherData.setCurrent(current);
        Location location = new Location();
        location.setName("Porya");
        weatherData.setLocation(location);
        when(restTemplate.getForObject(
                "https://api.weatherapi.com/v1/current.json?key=c8d70ac73e2f4639b04132653231203&aqi=no&q={coordinates}",
                WeatherData.class,
                latitude,
                longitude)
        ).thenReturn(weatherData);

        // when
        Recommendation recommendation = underTest.getWeatherInfo(latitude, longitude);

        // then
        String text = "It's too hot. Take some water";
        assertThat(recommendation.getRecommendation())
                .isNotNull()
                .isEqualTo(text);
    }

    @Test
    void shouldThrowExceptionWhenWeatherDataIsNull() {
        // given
        Double latitude = 66.766237;
        Double longitude = 33.632615;

        when(restTemplate.getForObject(
                "https://api.weatherapi.com/v1/current.json?key=c8d70ac73e2f4639b04132653231203&aqi=no&q={coordinates}",
                WeatherData.class,
                latitude,
                longitude)
        ).thenReturn(null);

        // when;
        // then
        assertThatThrownBy(() -> underTest.getWeatherInfo(latitude, longitude))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Weather data is null");
    }

    @Test
    void shouldAddAttraction() {
        // given
        Attraction attraction = new Attraction();
        Long localityId = 1L;
        Long serviceId1 = 1L;
        Long serviceId2 = 2L;
        List<Long> servicesIds = List.of(serviceId1, serviceId2);

        Locality locality = mock(Locality.class);
        Service service1 = mock(Service.class);
        Service service2 = mock(Service.class);
        when(localityRepository.getReferenceById(localityId)).thenReturn(locality);
        when(serviceRepository.getReferenceById(serviceId1)).thenReturn(service1);
        when(serviceRepository.getReferenceById(serviceId2)).thenReturn(service2);

        // when
        underTest.addAttraction(attraction, localityId, servicesIds);

        // then
        then(attractionRepository).should().save(attractionArgumentCaptor.capture());
        Attraction value = attractionArgumentCaptor.getValue();
        assertThat(value).isEqualTo(attraction);
        assertThat(value.getServices().size()).isEqualTo(2);
        assertThat(value.getLocality()).isEqualTo(locality);
    }

    @Test
    void shouldUpdateAttraction() {
        // given
        Long attractionId = 1L;
        Attraction attraction = new Attraction();
        attraction.setId(attractionId);
        Long serviceId = 1L;
        Service service = new Service();
        service.setId(serviceId);

        attraction.addService(service);
        String serviceAbbreviation = "qwe";

        ArgumentCaptor<Long> id = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> abbreviation = ArgumentCaptor.forClass(String.class);
        when(attractionRepository.findById(attractionId)).thenReturn(Optional.of(attraction));
        doNothing().when(serviceRepository).updateServiceByAbbreviation(abbreviation.capture(), id.capture());

        // when
        underTest.updateAttractionServicesAbbreviation(serviceAbbreviation, attractionId);

        // then
        verify(serviceRepository, times(1)).updateServiceByAbbreviation(serviceAbbreviation, serviceId);
        assertThat(serviceAbbreviation).isEqualTo(abbreviation.getValue());
        assertThat(serviceId).isEqualTo(id.getValue());
    }

    @Test
    void shouldThrowExceptionWhenAttractionToUpdateNotFound() {
        // given
        Long attractionId = 1L;
        when(attractionRepository.findById(attractionId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateAttractionServicesAbbreviation(null, attractionId))
                .isInstanceOf(AttractionNotFoundException.class)
                .hasMessageContaining(String.format("Attraction with id %s not found", attractionId));
    }

    @Test
    void shouldDeleteAttraction() {
        // given
        Attraction attraction = new Attraction();
        Long attractionId = 1L;
        Service service1 = mock(Service.class);
        Service service2 = mock(Service.class);

        attraction.addService(service1);
        attraction.addService(service2);

        when(attractionRepository.findById(attractionId)).thenReturn(Optional.of(attraction));
        doNothing().when(attractionRepository).deleteById(attractionId);

        // when
        assertThat(attraction.getServices().size()).isEqualTo(2);
        underTest.deleteAttraction(attractionId);

        // then
        verify(attractionRepository, times(1)).deleteById(attractionId);
        assertThat(attraction.getServices().size()).isEqualTo(0);
    }

    @Test
    void shouldThrowExceptionWhenAttractionToDeleteNotFound() {
        // given
        Long attractionId = 1L;
        when(attractionRepository.findById(attractionId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteAttraction(attractionId))
                .isInstanceOf(AttractionNotFoundException.class)
                .hasMessageContaining(String.format("Attraction with id %s not found", attractionId));
    }
}