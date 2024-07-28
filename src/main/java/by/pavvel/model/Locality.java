package by.pavvel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "Locality")
@Table(name = "locality")
public class Locality {

    @Id
    @SequenceGenerator(
            name = "locality_id_seq",
            sequenceName = "locality_id_seq",
            initialValue = 4
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "locality_id_seq"
    )
    @Column(name = "id",updatable = false)
    private Long id;

    @NotBlank(message = "{locality.title.blank}")
    @Size(min = 2,max = 50, message = "{locality.title.size}")
    @Column(name = "title",nullable = false)
    private String title;

    @Column(name = "population",nullable = false)
    @Min(value = 0, message = "{locality.population.value}")
    private Double population;

    @OneToMany(
            mappedBy = "locality",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private List<Attraction> attractions = new ArrayList<>();

    @NotNull(message = "{locality.metroAvailability.value}")
    @Column(name = "metro_availability", nullable = false)
    private Boolean metroAvailability;

    public Locality() {
    }

    public Locality(String title, Double population, Boolean metroAvailability) {
        this.title = title;
        this.population = population;
        this.metroAvailability = metroAvailability;
    }

    public Locality(String title, Double population, List<Attraction> attractions, Boolean metroAvailability) {
        this.title = title;
        this.population = population;
        this.attractions = attractions;
        this.metroAvailability = metroAvailability;
    }

    @Override
    public String toString() {
        return "Locality{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", population=" + population +
                ", attractions=" + attractions +
                ", metroAvailability=" + metroAvailability +
                '}';
    }
}
