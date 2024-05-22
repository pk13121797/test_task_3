package by.pavvel.service.impl;

import by.pavvel.exception.EmailAlreadyTakenException;
import by.pavvel.exception.PasswordsNotEqualException;
import by.pavvel.exception.TokenNotConfirmedException;
import by.pavvel.exception.UserNotFoundException;
import by.pavvel.model.change.ChangePasswordRequest;
import by.pavvel.model.change.VerificationToken;
import by.pavvel.model.reg.ApplicationUser;
import by.pavvel.model.reg.UserRole;
import by.pavvel.repository.UserRepository;
import by.pavvel.service.mail.MailSender;
import by.pavvel.service.mail.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private MailSender mailSender;

    @Captor
    private ArgumentCaptor<ApplicationUser> applicationUserArgumentCaptor;

    @InjectMocks
    private UserService underTest;

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        // given
        String email = "admin1gmail.com";
        ApplicationUser user = new ApplicationUser("John", "*****", email, UserRole.USER);
        when(userRepository.getUserByEmail(email)).thenReturn(Optional.of(user));

        // when
        UserDetails userDetails = underTest.loadUserByUsername(user.getEmail());

        // then
        assertThat(userDetails).isEqualTo(user);
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyTaken() {
        // given
        String email = "user@gmail.com";
        ApplicationUser user = new ApplicationUser("John", "*****", email, UserRole.USER);
        when(userRepository.getUserByEmail(email)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.loadUserByUsername(user.getEmail()))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(String.format(
                        "User with email %s not found", user.getEmail())
                );
    }

    @Test
    void shouldSignInSuccessfully() {
        // given
        String email = "user@gmail.com";
        ApplicationUser user = new ApplicationUser("John", "*****", email, UserRole.USER);
        when(userRepository.getUserByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user.getPassword())).thenReturn("12345");
        when(userRepository.save(user)).thenReturn(user);

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(20L),
                user
        );

        when(tokenService.generateVerificationToken(user)).thenReturn(verificationToken);

        String message = String.format("Hello, %s. Please, click on this link: " +
                        "http://localhost:8080/confirm-reg?token=%s to confirm registration.",
                user.getName(),
                token
        );
        doNothing().when(mailSender).sendMessage(email, "Account activation", message);

        // when
        underTest.signIn(user);

        // then
        then(userRepository).should().save(applicationUserArgumentCaptor.capture());
        ApplicationUser applicationUserArgumentCaptorValue = applicationUserArgumentCaptor.getValue();
        assertThat(applicationUserArgumentCaptorValue).isEqualTo(user);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyTaken() {
        // given
        String email = "user@gmail.com";
        ApplicationUser user = new ApplicationUser("John", "*****", email, UserRole.USER);
        when(userRepository.getUserByEmail(email)).thenReturn(Optional.of(user));

        // when
        // then
        assertThatThrownBy(() -> underTest.signIn(user))
                .isInstanceOf(EmailAlreadyTakenException.class)
                .hasMessageContaining("This email taken");
    }

    @Test
    void shouldResetPasswordSuccessfully() {
        // given
        String email = "user@gmail.com";
        ApplicationUser user = new ApplicationUser("John", "*****", email, UserRole.USER);
        when(userRepository.getUserByEmail(email)).thenReturn(Optional.of(user));

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(20L),
                user
        );

        when(tokenService.generateVerificationToken(user)).thenReturn(verificationToken);

        String message = String.format("Hello, %s. Please, click on this link: " +
                        "http://localhost:8080/confirm-reset?token=%s to confirm reset password.",
                user.getName(),
                verificationToken.getToken()
        );
        doNothing().when(mailSender).sendMessage(email, "Password reset", message);

        // when
        underTest.resetPassword(email);

        // then
        verify(mailSender, times(1)).sendMessage(email, "Password reset", message);
    }

    @Test
    void shouldThrowExceptionWhenResettingPassword() {
        // given
        String email = "user@gmail.com";
        when(userRepository.getUserByEmail(email)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.resetPassword(email))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(String.format("Cannot find user by email %s", email));
    }

    @Test
    void shouldChangePasswordSuccessfully() {
        // given
        String email = "user@gmail.com";
        String password = "*****";
        ApplicationUser user = new ApplicationUser("John", password, email, UserRole.USER);

        String newPassword = "test";
        String confirmPassword = "test";

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setConfirmedAt(LocalDateTime.now());
        String token = verificationToken.getToken();
        verificationToken.setApplicationUser(user);

        when(tokenService.getToken(token)).thenReturn(Optional.of(verificationToken));
        String encodedPassword = "12345";
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);
        doNothing()
                .when(userRepository)
                .changePassword(encodedPassword, email);

        String message = String.format("%s, your password has been successfully changed.", user.getEmail().toUpperCase());
        doNothing().when(mailSender).sendMessage(email, "Changing the password", message);

        ChangePasswordRequest passwordRequest = new ChangePasswordRequest(newPassword, confirmPassword);

        // when
        underTest.changePassword(passwordRequest, token);

        // then
        verify(userRepository, times(1)).changePassword(encodedPassword, email);
        verify(mailSender).sendMessage(email, "Changing the password", message);
    }

    @Test
    void shouldThrowExceptionWhenTokenIsNotConfirmed() {
        // given
        String newPassword = "test1";
        String confirmPassword = "test2";
        ChangePasswordRequest passwordRequest = new ChangePasswordRequest(newPassword, confirmPassword);

        VerificationToken verificationToken = new VerificationToken();
        String token = verificationToken.getToken();

        when(tokenService.getToken(token)).thenReturn(Optional.of(verificationToken));

        // when
        // then
        assertThatThrownBy(() -> underTest.changePassword(passwordRequest, token))
                .isInstanceOf(TokenNotConfirmedException.class)
                .hasMessageContaining(String.format(
                        "Token %s not confirmed",
                        token
                ));
    }

    @Test
    void shouldThrowExceptionWhenPasswordsNotEquals() {
        // given
        String newPassword = "test1";
        String confirmPassword = "test2";
        ChangePasswordRequest passwordRequest = new ChangePasswordRequest(newPassword, confirmPassword);

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setConfirmedAt(LocalDateTime.now());
        String token = verificationToken.getToken();

        when(tokenService.getToken(token)).thenReturn(Optional.of(verificationToken));

        // when
        // then
        assertThatThrownBy(() -> underTest.changePassword(passwordRequest, token))
                .isInstanceOf(PasswordsNotEqualException.class)
                .hasMessageContaining("Passwords are not equal");
    }

    @Test
    void checkPasswordReturnTrueWhenNewPasswordEqualsCurrentPassword() {
        // given
        String newPassword = "test";
        String confirmPassword = "test";
        ChangePasswordRequest passwordRequest = new ChangePasswordRequest(newPassword, confirmPassword);

        // when
        boolean b = underTest.checkPassword(passwordRequest);

        // then
        assertThat(b).isTrue();
    }

    @Test
    void checkPasswordReturnFalseWhenNewPasswordEqualsCurrentPassword() {
        // given
        String newPassword = "test1";
        String confirmPassword = "test2";
        ChangePasswordRequest passwordRequest = new ChangePasswordRequest(newPassword, confirmPassword);

        // when
        boolean b = underTest.checkPassword(passwordRequest);

        // then
        assertThat(b).isFalse();
    }

    @Test
    void shouldEnableAppUser() {
        // given
        String email = "user@gmail.com";
        doNothing()
                .when(userRepository)
                .enableAppUser(email);

        // when
        underTest.enableAppUser(email);

        // then
        verify(userRepository, times(1)).enableAppUser(email);
    }
}