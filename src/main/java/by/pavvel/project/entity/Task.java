package by.pavvel.project.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Task")
@Table(name = "task")
public class Task {

    @Id
    @SequenceGenerator(name = "task_sequence",
            sequenceName = "task_sequence",
            initialValue = 21, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "task_sequence")
    @Column(name = "id",updatable = false)
    private Long id;

    @NotBlank(message = "Title must have a value")
    @Size(min = 2,max = 50,message = "Title size should be between 2 and 50 characters")
    @Column(name = "title",nullable = false)
    private String title;

    @Column(name = "hours",nullable = false)
    @Max(value = 1000, message = "Max number of hours is 1000")
    private Integer hours;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "start_date",nullable = false)
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Future(message = "date should be in the future")
    @Column(name = "end_date",nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private Status status;

    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.DETACH,CascadeType.REFRESH})
    @JoinColumn(name = "project_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "project_fk"))
    private Project project;


    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH,CascadeType.REFRESH},
            fetch = FetchType.EAGER)
    @JoinTable(name = "task_employee",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id"))
    private List<Employee> employees = new ArrayList<>();

    public Task() {
    }

    public Task(String title, Integer hours, LocalDate startDate, LocalDate endDate, Status status) {
        this.title = title;
        this.hours = hours;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
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
