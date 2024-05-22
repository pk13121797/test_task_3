package by.pavvel.utils;

import by.pavvel.model.Task;
import by.pavvel.dto.TaskDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class TaskDtoMapper implements Function<Task, TaskDto> {

    @Override
    public TaskDto apply(Task task) {
        return new TaskDto(
                task.getId(),
                task.getProject(),
                task.getTitle(),
                task.getStartDate(),
                task.getEndDate(),
                task.getEmployees(),
                task.getStatus());
    }
}
