package by.pavvel.dto;

import by.pavvel.model.Employee;
import by.pavvel.model.Project;
import by.pavvel.model.Status;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;

@Getter
public class TaskDto {

    private final Long id;
    private final Project project;
    private final String title;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Set<Employee> employees;
    private final Status status;

    public TaskDto(Long id, Project project, String title, LocalDate startDate, LocalDate endDate, Set<Employee> employees, Status status) {
        this.id = id;
        this.project = project;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.employees = employees;
        this.status = status;
    }

    @Override
    public String toString() {
        return "TaskDto{" +
                "id=" + id +
                ", project=" + project +
                ", title='" + title + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", employees=" + employees +
                ", status=" + status +
                '}';
    }
}
