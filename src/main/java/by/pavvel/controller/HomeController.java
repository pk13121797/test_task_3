package by.pavvel.controller;

import by.pavvel.exception.PasswordsNotEqualException;
import by.pavvel.exception.TokenNotConfirmedException;
import by.pavvel.exception.TokenNotFoundException;
import by.pavvel.exception.UserNotFoundException;
import by.pavvel.model.change.ChangePasswordRequest;
import by.pavvel.service.impl.UserService;
import by.pavvel.service.mail.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(path = "/")
public class HomeController {

    private final UserService userService;

    private final TokenService tokenService;

    private final MessageSource messageSource;

    public HomeController(UserService userService, TokenService tokenService, MessageSource messageSource) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String mainPage(@AuthenticationPrincipal OAuth2User principal, Model model) {
        if (principal != null && principal.getAttributes().get("email") != null) {
            model.addAttribute("oauth2Username", principal.getAttributes().get("email"));
        } else if (principal != null && principal.getAttributes().get("login") != null) {
            model.addAttribute("oauth2Username", principal.getAttributes().get("login"));
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @PostMapping("/forgot")
    public String forgotPassword(@RequestParam("email") String email,
                                 RedirectAttributes redirectAttrs) {
        userService.resetPassword(email);

        String successMessage = messageSource.getMessage(
                "forgot.successMessage",
                new Object[]{},
                LocaleContextHolder.getLocale()
        );
        redirectAttrs.addFlashAttribute("successMessage", successMessage);
        return "redirect:/login";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFoundException(UserNotFoundException exception) {
        ModelAndView modelAndView = new ModelAndView("auth/login");

        String errorMessage = messageSource.getMessage(
                "userService.userNotFound",
                new Object[] {exception.getCause().getMessage()},
                LocaleContextHolder.getLocale()
        );
        modelAndView.addObject("errorMessage", errorMessage);
        return modelAndView;
    }

    @GetMapping("/confirm-reg")
    public String confirmToken(@RequestParam("token") String token) {
        tokenService.confirmToken(token);
        return "redirect:/login";
    }

    @GetMapping("/confirm-reset")
    public String confirmToken(@RequestParam("token") String token,
                               RedirectAttributes redirectAttrs) {
        tokenService.confirmToken(token);
        redirectAttrs.addFlashAttribute("token", token);
        return "redirect:/change";
    }

    @GetMapping("/change")
    public String getChangePasswordView(@ModelAttribute("request") ChangePasswordRequest changePasswordRequest) {
        return "auth/change";
    }

    @PostMapping("/change")
    public String changePassword(@ModelAttribute("request") @Valid ChangePasswordRequest changePasswordRequest,
                                 BindingResult bindingResult,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttrs,
                                 Model model) {
        String token = request.getParameterValues("token")[0];
        if (bindingResult.hasErrors()) {
            model.addAttribute("token", token);
            return "auth/change";
        }
        redirectAttrs.addFlashAttribute("token", token);
        userService.changePassword(changePasswordRequest, token);

        String successMessage = messageSource.getMessage(
                "change.successMessage",
                new Object[]{},
                LocaleContextHolder.getLocale()
        );
        redirectAttrs.addFlashAttribute("successMessage", successMessage);
        return "redirect:/login";
    }

    @ExceptionHandler({
            TokenNotFoundException.class,
            TokenNotConfirmedException.class,
            PasswordsNotEqualException.class
    })
    public String handleChangePasswordExceptions(RuntimeException exception,
                                                 HttpServletRequest request,
                                                 RedirectAttributes redirectAttrs) {
        String code;
        if (exception instanceof TokenNotFoundException) {
            code = "userService.tokenNotFound";
        } else if (exception instanceof TokenNotConfirmedException) {
            code = "userService.tokenNotConfirmed";
        } else {
            code = "userService.passwordsNotEqual";
        }

        String errorMessage = messageSource.getMessage(
                code,
                new Object[] {},
                LocaleContextHolder.getLocale()
        );

        String token = request.getParameterValues("token")[0];
        redirectAttrs.addFlashAttribute("token", token);
        redirectAttrs.addFlashAttribute("errorMessage", errorMessage);
        return "redirect:/change";
    }
}
