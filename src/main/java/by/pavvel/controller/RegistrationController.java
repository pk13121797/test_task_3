package by.pavvel.controller;

import by.pavvel.exception.EmailAlreadyTakenException;
import by.pavvel.model.reg.RegisterRequest;
import by.pavvel.service.impl.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(path = "/reg")
public class RegistrationController {

    private final RegistrationService registrationService;

    private final MessageSource messageSource;

    @Autowired
    public RegistrationController(RegistrationService registrationService, MessageSource messageSource) {
        this.registrationService = registrationService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String getRegistrationView(@ModelAttribute("request") RegisterRequest registerRequest){
        return "auth/reg";
    }

    @PostMapping
    public String register(@ModelAttribute("request") @Valid RegisterRequest registerRequest,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttrs) {
        if (bindingResult.hasErrors()) {
            return "auth/reg";
        }
        registrationService.register(registerRequest);

        String successMessage = messageSource.getMessage("reg.successMessage", new Object[]{}, LocaleContextHolder.getLocale());
        redirectAttrs.addFlashAttribute("successMessage", successMessage);
        return "redirect:/login";
    }

    @ExceptionHandler(EmailAlreadyTakenException.class)
    public String handleException(RedirectAttributes redirectAttrs) {
        String errorMessage = messageSource.getMessage(
                "userService.emailTaken",
                new Object[] {},
                LocaleContextHolder.getLocale()
        );
        redirectAttrs.addFlashAttribute("errorMessage", errorMessage);
        return "redirect:/reg";
    }
}
