package by.pavvel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity(name = "Service")
@Table(name = "service")
public class Service {

    @Id
    @SequenceGenerator(
            name = "service_id_seq",
            sequenceName = "service_id_seq",
            initialValue = 5
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "service_id_seq"
    )
    @Column(name = "id",updatable = false)
    private Long id;

    @NotBlank
    @Size(min = 2,max = 50)
    @Column(name = "title",nullable = false)
    private String title;

    @NotBlank
    @Size(min = 2,max = 5)
    @Column(name = "abbreviation",nullable = false)
    private String abbreviation;

    @ManyToMany(
            fetch = FetchType.LAZY, mappedBy = "services"
    )
    private Set<Attraction> attractions = new HashSet<>();

    public Service() {
    }

    public Service(String title, String abbreviation, Set<Attraction> attractions) {
        this.title = title;
        this.abbreviation = abbreviation;
        this.attractions = attractions;
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", attractions=" + attractions +
                '}';
    }
}
