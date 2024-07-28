package by.pavvel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity(name = "Attraction")
@Table(name = "attraction")
public class Attraction {

    @Id
    @SequenceGenerator(
            name = "attraction_id_seq",
            sequenceName = "attraction_id_seq",
            initialValue = 5
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "attraction_id_seq"
    )
    @Column(name = "id",updatable = false)
    private Long id;

    @NotBlank(message = "{attraction.title.blank}")
    @Size(min = 2,max = 50, message = "{attraction.title.size}")
    @Column(name = "title",nullable = false)
    private String title;

    @DateTimeFormat(pattern = "yyyy-MM-dd", iso = DateTimeFormat.ISO.DATE)
    @Column(name = "creation_date",nullable = false)
    private LocalDate creationDate;

    @NotBlank(message = "{attraction.description.blank}")
    @Size(min = 2,max = 50, message = "{attraction.description.size}")
    @Column(name = "description",nullable = false)
    private String description;

    @NotNull(message = "{attraction.type.blank}")
    @Enumerated(EnumType.STRING)
    @Column(name = "attraction_type",nullable = false)
    private AttractionType attractionType;

    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "locality_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "locality_fk")
    )
    private Locality locality;

    @ManyToMany(
            cascade = {
                    CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH,CascadeType.REFRESH
            },
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "attraction_service",
            joinColumns = @JoinColumn(name = "attraction_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    private Set<Service> services = new HashSet<>();

    public Attraction() {
    }

    public Attraction(String title, LocalDate creationDate, String description, AttractionType attractionType) {
        this.title = title;
        this.creationDate = creationDate;
        this.description = description;
        this.attractionType = attractionType;
    }

    public void addService(Service service) {
        this.services.add(service);
        service.getAttractions().add(this);
    }

    public void removeService(Service service) {
        this.services.remove(service);
        service.getAttractions().remove(this);
    }
}
