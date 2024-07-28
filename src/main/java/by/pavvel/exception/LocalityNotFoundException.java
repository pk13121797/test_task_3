package by.pavvel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class LocalityNotFoundException extends RuntimeException {

    public LocalityNotFoundException(String message) {
        super(message);
    }
}
