package by.pavvel.repository;

import by.pavvel.model.Status;
import by.pavvel.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {

    @Query(
            value = "select t from Task t left join fetch t.project",
            countQuery = "select count(*) from Task t"
    )
    Page<Task> findAllWithProjectPageable(Pageable pageable);

    @Query(
            value = "select t from Task t left join fetch t.employees",
            countQuery = "select count(*) from Task t"
    )
    Page<Task> findAllWithEmployeesPageable(Pageable pageable);

    @Modifying
    @Query("update Task t set t.title =?1, t.hours =?2, t.startDate =?3, t.endDate =?4, t.status =?5, t.project.id =?6 where t.id =?7")
    void updateTask(String taskTitle, Integer hours, LocalDate startDate, LocalDate endDate, Status status, Long projectId, Long taskId);

    @Modifying
    @Query("delete from Task t where t.id =?1")
    void deleteTaskById(Long id);
}
