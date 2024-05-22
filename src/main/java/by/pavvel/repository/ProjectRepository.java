package by.pavvel.repository;

import by.pavvel.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

    @Query("select p from Project p left join fetch p.tasks where p.id = ?1")
    Optional<Project> findProjectById(Long id);

    @Modifying
    @Query("update Project p set p.title =?1,p.abbreviation =?2,p.description =?3 where p.id = ?4")
    void updateProject(String title,String abbreviation,String description,Long id);

    @Modifying
    @Query("delete from Project p where p.id =?1")
    void deleteProjectById(Long id);

    @Query("select p from Project p where p.title = ?1")
    Optional<Project> findProjectByTitle(String title);
}
