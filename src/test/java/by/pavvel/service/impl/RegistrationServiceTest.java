package by.pavvel.service.impl;

import by.pavvel.exception.EmailNotValidException;
import by.pavvel.model.reg.ApplicationUser;
import by.pavvel.model.reg.RegisterRequest;
import by.pavvel.model.reg.UserRole;
import by.pavvel.utils.EmailValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class RegistrationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private EmailValidator emailValidator;

    @Captor
    private ArgumentCaptor<ApplicationUser> applicationUserCaptor;

    private AutoCloseable autoCloseable;

    private RegistrationService underTest;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new RegistrationService(emailValidator, userService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldNotRegisterWhenEmailIsInvalid() {
        // given
        String email = "admin1gmail.com";
        RegisterRequest registerRequest = new RegisterRequest("John", email, "123");
        when(emailValidator.test(email)).thenReturn(false);

        // when
        // then
        assertThatThrownBy(() -> underTest.register(registerRequest))
                .isInstanceOf(EmailNotValidException.class)
                .hasMessageContaining(String.format(
                        "Email %s not valid",email)
                );
    }

    @Test
    void shouldRegisterSuccessfully() {
        // given
        String email = "user@gmail.com";
        RegisterRequest registerRequest = new RegisterRequest("John", email, "123");

        ApplicationUser user = new ApplicationUser(
                registerRequest.getName(),
                registerRequest.getPassword(),
                registerRequest.getEmail(),
                UserRole.USER);
        when(emailValidator.test(email)).thenReturn(true);

        // when
        underTest.register(registerRequest);

        // then
        then(userService).should().signIn(applicationUserCaptor.capture());
        ApplicationUser applicationUserArgumentCaptorValue = applicationUserCaptor.getValue();
        assertThat(applicationUserArgumentCaptorValue).isEqualTo(user);
    }
}