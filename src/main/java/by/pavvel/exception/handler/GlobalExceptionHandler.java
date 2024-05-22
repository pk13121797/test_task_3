package by.pavvel.exception.handler;

import by.pavvel.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    static final String DEFAULT_ERROR_VIEW = "errors/error";

    @ExceptionHandler({
            EmployeeNotFoundException.class,
            ProjectNotFoundException.class,
            TaskNotFoundException.class
    })
    public ModelAndView handleNotFoundException(HttpServletRequest request, RuntimeException e) {
        return getModelAndView(request, e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ModelAndView handleAnyRuntimeException(HttpServletRequest request, RuntimeException e) {
        return getModelAndView(request, e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static ModelAndView getModelAndView(HttpServletRequest request, Exception e, HttpStatus httpStatus) {
        ModelAndView modelAndView = new ModelAndView(DEFAULT_ERROR_VIEW);
        modelAndView.addObject("errorMessage",e.getMessage());
        modelAndView.addObject("httpStatus", httpStatus);
        modelAndView.addObject("zonedDateTime", ZonedDateTime.now());
        modelAndView.addObject("url", request.getRequestURL());
        return modelAndView;
    }
}
