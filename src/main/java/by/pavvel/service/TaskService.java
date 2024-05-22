package by.pavvel.service;

import by.pavvel.dto.TaskDto;
import by.pavvel.model.Task;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TaskService {

    Page<TaskDto> showTasks(int pageNo, String field, String direction);

    List<Task> getTasks();

    List<Task> getTasksById(List<Long> taskId);

    Task getTask(Long taskId);

    void addTask(Task task, Long projectId, List<Long> employeesIds);

    void updateTask(Task task, Long projectId, List<Long> employeesIds);

    void deleteTask(Long taskId);

    Task getTaskProxy(Long taskId);

    List<Long> getSelectedEmployeeIds(Long taskId);
}
