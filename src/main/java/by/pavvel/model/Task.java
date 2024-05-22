package by.pavvel.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity(name = "Task")
@Table(name = "task")
public class Task {

    @Id
    @SequenceGenerator(
            name = "task_sequence",
            sequenceName = "task_sequence",
            allocationSize = 1,
            initialValue = 21
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "task_sequence"
    )
    @Column(name = "id",updatable = false)
    private Long id;

    @NotBlank(message = "{task.title.blank}")
    @Size(min = 2,max = 50,message = "{task.title.size}")
    @Column(name = "title",nullable = false)
    private String title;

    @Column(name = "hours",nullable = false)
    @Max(value = 1000, message = "{task.hours.max}")
    private Integer hours;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "start_date",nullable = false)
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Future(message = "{task.endDate.future}")
    @Column(name = "end_date",nullable = false)
    private LocalDate endDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private Status status;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "project_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "project_fk")
    )
    private Project project;

    @ManyToMany(
            cascade = {
                    CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH,CascadeType.REFRESH
            },
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "task_employee",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<Employee> employees = new HashSet<>();

    public Task() {
    }

    public Task(String title, Integer hours, LocalDate startDate, LocalDate endDate, Status status) {
        this.title = title;
        this.hours = hours;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public void addEmployee(Employee employee) {
        this.employees.add(employee);
        employee.getTasks().add(this);
    }

    public void removeEmployee(Employee employee) {
        this.employees.remove(employee);
        employee.getTasks().remove(this);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", hours=" + hours +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status=" + status +
                ", project=" + project +
                ", employees=" + employees +
                '}';
    }
}
