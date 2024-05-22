package by.pavvel.service.impl;

import by.pavvel.exception.EmailNotValidException;
import by.pavvel.model.reg.ApplicationUser;
import by.pavvel.model.reg.RegisterRequest;
import by.pavvel.model.reg.UserRole;
import by.pavvel.utils.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private static final Logger logger = LogManager.getLogger(RegistrationService.class);

    private final EmailValidator emailValidator;

    private final UserService userService;

    public RegistrationService(EmailValidator emailValidator, UserService userService) {
        this.emailValidator = emailValidator;
        this.userService = userService;
    }

    @Transactional
    public void register(RegisterRequest registerRequest){
        logger.info("register was called: {}", registerRequest.getEmail());

        String email = registerRequest.getEmail();
        boolean isValid = emailValidator.test(email);
        if (!isValid){
            EmailNotValidException emailNotValidException = new EmailNotValidException(String.format("Email %s not valid", email));
            logger.error("error in register: {}", registerRequest.getEmail(), emailNotValidException);
            throw emailNotValidException;
        }

        userService.signIn(new ApplicationUser(
                registerRequest.getName(),
                registerRequest.getPassword(),
                email,
                UserRole.USER
        ));
    }
}
