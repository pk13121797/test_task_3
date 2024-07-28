package by.pavvel.service;

import by.pavvel.dto.LocalityDto;
import by.pavvel.exception.LocalityNotFoundException;
import by.pavvel.model.Locality;
import by.pavvel.repository.LocalityRepository;
import by.pavvel.service.impl.LocalityServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class LocalityServiceTest {

    @Mock
    private LocalityRepository localityRepository;

    @Captor
    private ArgumentCaptor<Locality> localityArgumentCaptor;

    private AutoCloseable autoCloseable;

    private LocalityService underTest;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new LocalityServiceImpl(localityRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldAddLocality() {
        // given
        Locality locality = new Locality();

        // when
        underTest.addLocality(locality);

        // then
        then(localityRepository).should().save(localityArgumentCaptor.capture());
        Locality value = localityArgumentCaptor.getValue();
        assertThat(value).isEqualTo(locality);
    }

    @Test
    void shouldUpdateLocality() {
        // given
        Long localityId = 1L;
        Locality locality = new Locality();
        locality.setId(localityId);

        Boolean metroAvailabilityToUpdate = false;
        Double populationToUpdate = 0.0;
        LocalityDto localityDto = new LocalityDto(
                populationToUpdate, metroAvailabilityToUpdate
        );

        ArgumentCaptor<Long> id = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Double> population = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Boolean> metroAvailability = ArgumentCaptor.forClass(Boolean.class);
        when(localityRepository.findById(localityId)).thenReturn(Optional.of(locality));
        doNothing().when(localityRepository).updateLocality(population.capture(), metroAvailability.capture(), id.capture());

        // when
        underTest.updateLocalityInfo(localityDto, localityId);

        // then
        verify(localityRepository, times(1)).updateLocality(populationToUpdate, metroAvailabilityToUpdate, localityId);
        assertThat(localityId).isEqualTo(id.getValue());
        assertThat(populationToUpdate).isEqualTo(population.getValue());
        assertThat(metroAvailabilityToUpdate).isEqualTo(metroAvailability.getValue());
    }

    @Test
    void shouldThrowExceptionWhenLocalityToUpdateNotFound() {
        // given
        Long localityId = 1L;
        when(localityRepository.findById(localityId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.updateLocalityInfo(null, localityId))
                .isInstanceOf(LocalityNotFoundException.class)
                .hasMessageContaining(String.format("Locality with id %s not found", localityId));
    }
}