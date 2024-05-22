package by.pavvel.model;

import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity(name = "Employee")
@Table(name = "employee")
public class Employee {

    @Id
    @SequenceGenerator(
            name = "employee_sequence",
            sequenceName = "employee_sequence",
            allocationSize = 1,
            initialValue = 16
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "employee_sequence"
    )
    @Column(name = "id",updatable = false)
    private Long id;

    @NotBlank(message = "{employee.name.blank}")
    @Size(min = 2,max = 50,message = "{employee.name.size}")
    @Column(name = "name",nullable = false)
    private String name;

    @NotBlank(message = "{employee.surname.blank}")
    @Size(min = 2,max = 50,message = "{employee.surname.size}")
    @Column(name = "surname",nullable = false)
    private String surname;

    @NotBlank(message = "{employee.middleName.blank}")
    @Size(min = 2,max = 50,message = "{employee.middleName.size}")
    @Column(name = "middle_name",nullable = false)
    private String middleName;

    @NotBlank(message = "{employee.post.blank}")
    @Size(min = 3,max = 20,message = "{employee.post.size}")
    @Column(name = "post",nullable = false)
    private String post;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "images",
            joinColumns = @JoinColumn(
                    name = "user_id"
            )
    )
    @Column(name = "file_name")
    private Set<String> images = new HashSet<>();

    @ManyToMany(
            fetch = FetchType.LAZY, mappedBy = "employees"
    )
    private Set<Task> tasks = new HashSet<>();

    public Employee() {
    }

    public Employee(String name, String surname, String middleName, String post) {
        this.name = name;
        this.surname = surname;
        this.middleName = middleName;
        this.post = post;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", middleName='" + middleName + '\'' +
                ", post='" + post + '\'' +
                '}';
    }
}
