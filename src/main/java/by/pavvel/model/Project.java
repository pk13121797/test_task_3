package by.pavvel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "Project")
@Table(name = "project")
public class Project {

    @Id
    @SequenceGenerator(
            name = "project_sequence",
            sequenceName = "project_sequence",
            allocationSize = 1,
            initialValue = 11
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "project_sequence"
    )
    @Column(name = "id",updatable = false)
    private Long id;

    @NotBlank(message = "{project.title.blank}")
    @Size(min = 2,max = 50,message = "{project.title.size}")
    @Column(name = "title",nullable = false)
    private String title;

    @NotBlank(message = "{project.abbreviation.blank}")
    @Size(min = 2,max = 5,message = "{project.abbreviation.size}")
    @Column(name = "abbreviation",nullable = false)
    private String abbreviation;

    @NotBlank(message = "{project.description.blank}")
    @Size(min = 2,max = 50,message = "{project.description.size}")
    @Column(name = "description",nullable = false)
    private String description;

    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<Task> tasks = new ArrayList<>();

    public Project() {
    }

    public Project(String title, String abbreviation, String description) {
        this.title = title;
        this.abbreviation = abbreviation;
        this.description = description;
    }

    public void addTask(Task task) {
        this.tasks.add(task);
        task.setProject(this);
    }

    public void removeTask(Task task) {
        this.tasks.remove(task);
        task.setProject(null);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
