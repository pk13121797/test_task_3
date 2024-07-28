package by.pavvel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Recommendation {

    private final UUID id;

    private final String recommendation;

    private final String text;

    private final String address;

    @JsonFormat(pattern="yyyy-MM-dd")
    private final LocalDateTime localDateTime;

    public Recommendation(UUID id, String recommendation, String text, String address, LocalDateTime localDateTime) {
        this.id = id;
        this.recommendation = recommendation;
        this.text = text;
        this.address = address;
        this.localDateTime = localDateTime;
    }
}
