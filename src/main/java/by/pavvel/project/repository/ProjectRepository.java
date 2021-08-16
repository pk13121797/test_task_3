package by.pavvel.project.repository;

import by.pavvel.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

    @Modifying
    @Query("update Project p set p.title =?1,p.abbreviation =?2,p.description =?3 where p.id = ?4")
    void updateProject(String title,String abbreviation,String description,Long id);

    Optional<Project> findProjectByTitle(String title);

    @Modifying
    @Query("delete from Project p where p.id =?1")
    void deleteProjectsById(Long id);
}
