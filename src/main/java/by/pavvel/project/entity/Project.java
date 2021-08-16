package by.pavvel.project.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Project")
@Table(name = "project")
public class Project {

    @Id
    @SequenceGenerator(name = "project_sequence",
            sequenceName = "project_sequence",
            initialValue = 11, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "project_sequence")
    @Column(name = "id",updatable = false)
    private Long id;

    @NotBlank(message = "Title must have a value")
    @Size(min = 2,max = 50,message = "Title size should be between 2 and 50 characters")
    @Column(name = "title",nullable = false)
    private String title;

    @NotBlank(message = "Abbreviation must have a value")
    @Size(min = 2,max = 5,message = "Abbreviation size should be between 2 and 5 characters")
    @Column(name = "abbreviation",nullable = false)
    private String abbreviation;

    @NotBlank(message = "Description must have a value")
    @Size(min = 2,max = 50,message = "Description size should be between 2 and 50 characters")
    @Column(name = "description",nullable = false)
    private String description;

    @OneToMany(mappedBy = "project",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Task> tasks = new ArrayList<>();

    public Project() {
    }

    public Project(String title, String abbreviation, String description) {
        this.title = title;
        this.abbreviation = abbreviation;
        this.description = description;
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

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", description='" + description + '\'' +
                ", tasks=" + tasks +
                '}';
    }
}
