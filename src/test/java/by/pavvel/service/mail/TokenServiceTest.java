package by.pavvel.service.mail;

import by.pavvel.model.change.VerificationToken;
import by.pavvel.model.reg.ApplicationUser;
import by.pavvel.model.reg.UserRole;
import by.pavvel.repository.TokenRepository;
import by.pavvel.service.impl.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private Clock clock;

    @Captor
    private ArgumentCaptor<VerificationToken> verificationTokenArgumentCaptor;
    @InjectMocks
    private TokenService underTest;

    private static final ZonedDateTime ZONED_DATE_TIME = ZonedDateTime.of(
            2024, 3, 2, 9, 15, 30, 0, ZoneId.of("Europe/Minsk")
    );

    @Test
    void shouldConfirmTokenSuccessfully() {
        // given
        String email = "user@gmail.com";
        ApplicationUser user = new ApplicationUser("John", "*****", email, UserRole.USER);

        when(clock.getZone()).thenReturn(ZONED_DATE_TIME.getZone());
        when(clock.instant()).thenReturn(ZONED_DATE_TIME.toInstant());
        String token = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.of(2024, 3, 2, 9, 15, 30, 0);
        VerificationToken verificationToken = new VerificationToken(
                token,
                now,
                now.plusMinutes(20L),
                user
        );

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
        doNothing().when(tokenRepository).updateConfirmedAt(token, now);
        doNothing().when(userService).enableAppUser(email);

        // when
        underTest.confirmToken(token);

        // then
        verify(tokenRepository, times(1)).updateConfirmedAt(token, now);
        verify(userService, times(1)).enableAppUser(email);
    }

    @Test
    void shouldThrowExceptionWhenTokenNotFound() {
        // given
        String token = UUID.randomUUID().toString();
        when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.confirmToken(token))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Token %s not found",token));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyConfirmed() {
        // given
        String email = "user@gmail.com";
        ApplicationUser user = new ApplicationUser("John", "*****", email, UserRole.USER);

        String token = UUID.randomUUID().toString();
        LocalDateTime tokenCreated = LocalDateTime.of(2024, 3, 2, 9, 15, 30, 0);
        VerificationToken verificationToken = new VerificationToken(
                token,
                tokenCreated,
                tokenCreated.plusMinutes(20),
                user
        );

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
        verificationToken.setConfirmedAt(tokenCreated);

        // when
        // then
        assertThatThrownBy(() -> underTest.confirmToken(token))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Email already confirmed");

        verify(tokenRepository, times(0)).updateConfirmedAt(token, tokenCreated);
        verify(userService, times(0)).enableAppUser(email);
    }

    @Test
    void shouldThrowExceptionWhenTokenExpired() {
        // given
        String email = "user@gmail.com";
        ApplicationUser user = new ApplicationUser("John", "*****", email, UserRole.USER);

        when(clock.getZone()).thenReturn(ZONED_DATE_TIME.getZone());
        when(clock.instant()).thenReturn(ZONED_DATE_TIME.toInstant());
        String token = UUID.randomUUID().toString();
        LocalDateTime tokenCreated = LocalDateTime.of(2024, 3, 2, 9, 15, 30, 0);
        LocalDateTime tokenExpired = tokenCreated.minusNanos(1);
        VerificationToken verificationToken = new VerificationToken(
                token,
                tokenCreated,
                tokenCreated.minusNanos(1),
                user
        );

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
        verificationToken.setExpired(tokenExpired);

        // when
        // then
        assertThatThrownBy(() -> underTest.confirmToken(token))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Token %s expired",token));

        verify(tokenRepository, times(0)).updateConfirmedAt(token, tokenCreated);
        verify(userService, times(0)).enableAppUser(email);
    }

    @Test
    void shouldGenerateTokenSuccessfully() {
        // given
        String email = "user@gmail.com";
        ApplicationUser user = new ApplicationUser("John", "*****", email, UserRole.USER);
        when(clock.getZone()).thenReturn(ZONED_DATE_TIME.getZone());
        when(clock.instant()).thenReturn(ZONED_DATE_TIME.toInstant());

        // when
        VerificationToken generatedVerificationToken = underTest.generateVerificationToken(user);

        // then
        then(tokenRepository).should().save(verificationTokenArgumentCaptor.capture());
        VerificationToken verificationTokenArgumentCaptorValue = verificationTokenArgumentCaptor.getValue();
        assertThat(verificationTokenArgumentCaptorValue).isEqualTo(generatedVerificationToken);
    }

    @Test
    void shouldSaveToken() {
        // given
        String email = "user@gmail.com";
        ApplicationUser user = new ApplicationUser("John", "*****", email, UserRole.USER);

        String token = UUID.randomUUID().toString();
        LocalDateTime tokenCreated = LocalDateTime.of(2024, 3, 2, 9, 15, 30, 0);
        VerificationToken verificationToken = new VerificationToken(
                token,
                tokenCreated,
                tokenCreated.minusNanos(1),
                user
        );

        // when
        underTest.addToken(verificationToken);

        // then
        then(tokenRepository).should().save(verificationTokenArgumentCaptor.capture());
        VerificationToken verificationTokenArgumentCaptorValue = verificationTokenArgumentCaptor.getValue();
        assertThat(verificationTokenArgumentCaptorValue).isEqualTo(verificationToken);
    }

    @Test
    void shouldGetOptionalToken() {
        // given
        String email = "user@gmail.com";
        ApplicationUser user = new ApplicationUser("John", "*****", email, UserRole.USER);

        String token = UUID.randomUUID().toString();
        LocalDateTime tokenCreated = LocalDateTime.of(2024, 3, 2, 9, 15, 30, 0);
        VerificationToken verificationToken = new VerificationToken(
                token,
                tokenCreated,
                tokenCreated.plusMinutes(20),
                user
        );

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));

        // when
        Optional<VerificationToken> verificationTokenOptional = underTest.getToken(token);

        // then
        assertThat(verificationTokenOptional.get()).isEqualTo(verificationToken);
    }

    @Test
    void shouldConfirmToken() {
        // given
        String email = "user@gmail.com";
        ApplicationUser user = new ApplicationUser("John", "*****", email, UserRole.USER);
        when(clock.getZone()).thenReturn(ZONED_DATE_TIME.getZone());
        when(clock.instant()).thenReturn(ZONED_DATE_TIME.toInstant());

        String token = UUID.randomUUID().toString();
        LocalDateTime tokenCreated = LocalDateTime.of(2024, 3, 2, 9, 15, 30, 0);
        VerificationToken verificationToken = new VerificationToken(
                token,
                tokenCreated,
                tokenCreated.plusMinutes(20),
                user
        );

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(verificationToken));
        doNothing().when(tokenRepository).updateConfirmedAt(token, tokenCreated);

        // when
        underTest.confirmToken(token);

        // then
        verify(tokenRepository, times(1)).updateConfirmedAt(token, tokenCreated);
    }
}
