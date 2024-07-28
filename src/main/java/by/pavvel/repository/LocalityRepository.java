package by.pavvel.repository;

import by.pavvel.model.Locality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalityRepository extends JpaRepository<Locality, Long> {

    @Modifying
    @Query("update Locality l set l.population =?1, l.metroAvailability = ?2 where l.id = ?3")
    void updateLocality(Double population, Boolean metroAvailability, Long localityId);
}
