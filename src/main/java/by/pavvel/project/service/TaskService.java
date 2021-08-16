package by.pavvel.project.service;

import by.pavvel.project.entity.Task;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TaskService {

    Page<Task> showTasks(int pageNo, int pageSize, String field, String direction);

    List<Task> getTasks();

    List<Task> getTasksById(List<Long> id);

    Task getTask(Long id);

    void addTask(Task task);

    void updateTask(Task task);

    void deleteTask(Long id);
}
