package by.pavvel.repository;

import by.pavvel.dto.AttractionLocalityDto;
import by.pavvel.model.Attraction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttractionRepository extends JpaRepository<Attraction, Long> {

    @Query("select a from Attraction a where upper(a.attractionType) = upper(?1)")
    List<Attraction> findAllSortedAttractions(String type, Sort sort);

    @Query("select new by.pavvel.dto.AttractionLocalityDto(a.id, a.title, a.creationDate, a.description, a.attractionType, l.population, l.metroAvailability) " +
           "from Attraction a left join a.locality l " +
           "where lower(l.title) = lower(?1)")
    List<AttractionLocalityDto> findAllAttractionsByLocality(String locality);

    @Query("select a from Attraction a left join fetch a.services where a.id = ?1")
    Optional<Attraction> findAttractionWithServices(Long attractionId);
}
