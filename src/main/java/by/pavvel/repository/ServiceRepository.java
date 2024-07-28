package by.pavvel.repository;

import by.pavvel.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    @Modifying
    @Query("update Service s set s.abbreviation =?1 where s.id = ?2")
    void updateServiceByAbbreviation(String serviceAbbreviation, Long serviceId);
}
