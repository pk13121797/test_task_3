package by.pavvel.service.impl;

import by.pavvel.exception.*;
import by.pavvel.model.change.ChangePasswordRequest;
import by.pavvel.model.change.VerificationToken;
import by.pavvel.model.reg.ApplicationUser;
import by.pavvel.repository.UserRepository;
import by.pavvel.service.mail.MailSender;
import by.pavvel.service.mail.TokenService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    private final PasswordEncoder passwordEncoder;

    private final MailSender mailSender;

    private final TokenService tokenService;

    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder, MailSender mailSender, TokenService tokenService, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("loadUserByUsername was called: {}", username);
        return userRepository.getUserByEmail(username).orElseThrow(() -> {
            UsernameNotFoundException usernameNotFoundException = new UsernameNotFoundException(String.format("User with email %s not found", username));
            logger.error("error in loadUserByUsername: {}", username, usernameNotFoundException);
            return usernameNotFoundException;
        });
    }

    public void signIn(ApplicationUser user) {
        logger.info("signIn was called: {}", user.getEmail());

        if (userRepository.getUserByEmail(user.getEmail()).isPresent()) {
            EmailAlreadyTakenException emailAlreadyTakenException = new EmailAlreadyTakenException("This email taken");
            logger.error("error in signIn: {}", user.getEmail(), emailAlreadyTakenException);
            throw emailAlreadyTakenException;
        }

        String encode = passwordEncoder.encode(user.getPassword());
        user.setPassword(encode);
        userRepository.save(user);

        VerificationToken verificationToken = tokenService.generateVerificationToken(user);

        String message = String.format("Hello, %s. Please, click on this link: " +
                "http://localhost:8080/confirm-reg?token=%s to confirm registration.",
                user.getName(),
                verificationToken.getToken()
        );
        mailSender.sendMessage(user.getEmail(), "Account activation", message);
    }

    public void resetPassword(String email) {

        logger.info("resetPassword was called: {}", email);

        ApplicationUser applicationUser = userRepository.getUserByEmail(email).orElseThrow(() -> {
            UserNotFoundException userNotFoundException = new UserNotFoundException(String.format("Cannot find user by email %s", email), new Throwable(email));
            logger.error("error in resetPassword: {}", email, userNotFoundException);
            return userNotFoundException;
        });

        VerificationToken verificationToken = tokenService.generateVerificationToken(applicationUser);

        String message = String.format("Hello, %s. Please, click on this link: " +
                        "http://localhost:8080/confirm-reset?token=%s to confirm reset password.",
                applicationUser.getName(),
                verificationToken.getToken()
        );
        mailSender.sendMessage(applicationUser.getEmail(), "Password reset", message);
    }

    public void changePassword(ChangePasswordRequest request, String token) {
        logger.info("changePassword was called:");

        VerificationToken verificationToken = tokenService.getToken(token).orElseThrow(() -> {
            TokenNotFoundException tokenNotFoundException = new TokenNotFoundException(String.format("Token %s not found", token));
            logger.error("error in changePasswordPassword:", tokenNotFoundException);
            return tokenNotFoundException;
        });

        if (verificationToken.getConfirmedAt() == null) {
            TokenNotConfirmedException tokenNotConfirmedException = new TokenNotConfirmedException(String.format("Token %s not confirmed", verificationToken.getToken()));
            logger.error("error in changePasswordPassword:", tokenNotConfirmedException);
            throw tokenNotConfirmedException;
        }

        boolean isEquals = checkPassword(request);
        if (!isEquals) {
            PasswordsNotEqualException passwordsAreNotEqual = new PasswordsNotEqualException("Passwords are not equal");
            logger.error("error in changePasswordPassword:", passwordsAreNotEqual);
            throw passwordsAreNotEqual;
        }

        ApplicationUser applicationUser = verificationToken.getApplicationUser();

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        userRepository.changePassword(encodedPassword, applicationUser.getUsername());

        String message = String.format("%s, your password has been successfully changed.",applicationUser.getUsername().toUpperCase());
        mailSender.sendMessage(applicationUser.getEmail(), "Changing the password", message);
    }

    public boolean checkPassword(ChangePasswordRequest request) {
        logger.info("checkPassword was called:");
        return request.getNewPassword().equals(request.getConfirmPassword());
    }

    public void enableAppUser(String email) {
        logger.info("enableAppUser was called: {}", email);
        userRepository.enableAppUser(email);
    }
}
