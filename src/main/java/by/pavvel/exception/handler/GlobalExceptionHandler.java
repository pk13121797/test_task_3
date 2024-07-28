package by.pavvel.exception.handler;

import by.pavvel.exception.AttractionNotFoundException;
import by.pavvel.exception.LocalityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(BindException exception, HttpServletRequest request) {

        BindingResult result = exception.getBindingResult();
        List<String> errorMessages = result.getAllErrors()
                .stream()
                .map(objectError -> messageSource.getMessage(objectError, LocaleContextHolder.getLocale()))
                .toList();

        ApiException apiException = new ApiException(
                errorMessages,
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now(),
                request.getServletPath()
        );
        return new ResponseEntity<>(apiException, apiException.getHttpStatus());
    }

    @ExceptionHandler({
            LocalityNotFoundException.class,
            AttractionNotFoundException.class
    })
    public ResponseEntity<Object> handleNotFoundException(RuntimeException exception, HttpServletRequest request) {

        ApiException apiException = new ApiException(
                List.of(exception.getLocalizedMessage()),
                HttpStatus.NOT_FOUND,
                LocalDateTime.now(),
                request.getServletPath()
        );
        return new ResponseEntity<>(apiException, apiException.getHttpStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleApiRequestException(RuntimeException exception, HttpServletRequest request) {

        ApiException apiException = new ApiException(
                List.of(exception.getLocalizedMessage()),
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now(),
                request.getServletPath()
        );
        return new ResponseEntity<>(apiException, apiException.getHttpStatus());
    }
}
