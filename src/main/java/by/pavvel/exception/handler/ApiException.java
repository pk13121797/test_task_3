package by.pavvel.exception.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ApiException {

    private final List<String> message;

    private final HttpStatus httpStatus;

    @JsonFormat(pattern="yyyy-MM-dd")
    private final LocalDateTime localDate;

    private final String path;

    public ApiException(List<String> message, HttpStatus httpStatus, LocalDateTime localDate, String path) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.localDate = localDate;
        this.path = path;
    }

    @Override
    public String toString() {
        return "ApiException{" +
                "message=" + message +
                ", httpStatus=" + httpStatus +
                ", localDate=" + localDate +
                ", path='" + path + '\'' +
                '}';
    }
}
