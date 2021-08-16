package by.pavvel.project.entity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Employee")
@Table(name = "employee")
public class Employee {

    @Id
    @SequenceGenerator(name = "employee_sequence",
            sequenceName = "employee_sequence",
            initialValue = 21, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "employee_sequence")
    @Column(name = "id",updatable = false)
    private Long id;

    @NotBlank(message = "Name must have a value")
    @Size(min = 2,max = 50,message = "Name size should be between 2 and 50 characters")
    @Column(name = "name",nullable = false)
    private String name;

    @NotBlank(message = "Surname must have a value")
    @Size(min = 2,max = 50,message = "Surname size should be between 2 and 50 characters")
    @Column(name = "surname",nullable = false)
    private String surname;

    @NotBlank(message = "Middle name must have a value")
    @Size(min = 2,max = 50,message = "Middle name size should be between 2 and 50 characters")
    @Column(name = "middle_name",nullable = false)
    private String middleName;

    @NotBlank(message = "Post must have a value")
    @Size(min = 3,max = 20,message = "Post size should be between 3 and 20 characters")
    @Column(name = "post",nullable = false)
    private String post;

    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH,CascadeType.REFRESH},
            fetch = FetchType.EAGER)
    @JoinTable(name = "task_employee",
            joinColumns = @JoinColumn(name = "employee_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id"))
    private List<Task> tasks = new ArrayList<>();

    public Employee() {
    }

    public Employee(String name, String surname, String middleName, String post) {
        this.name = name;
        this.surname = surname;
        this.middleName = middleName;
        this.post = post;
    }


    public void addTask(Task task){
        tasks.add(task);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", middleName='" + middleName + '\'' +
                ", post='" + post + '\'' +
                ", tasks=" + tasks +
                '}';
    }
}
