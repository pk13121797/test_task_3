package by.pavvel.project.repository;

import by.pavvel.project.entity.Project;
import by.pavvel.project.entity.Status;
import by.pavvel.project.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

    @Modifying
    @Query("update Task t set t.title =?1,t.hours =?2,t.startDate =?3," +
            "t.endDate =?4,t.status =?5,t.project =?6 where t.id =?7")
    void updateTask(String taskTitle, Integer hours,
                    LocalDate startDate, LocalDate endDate, Status status, Project project, Long id);

    @Modifying
    @Query("delete from Task t where t.id =?1")
    void deleteTasksById(Long id);
}
