package by.pavvel.service.mail;

import by.pavvel.model.change.VerificationToken;
import by.pavvel.model.reg.ApplicationUser;
import by.pavvel.repository.TokenRepository;
import by.pavvel.service.impl.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TokenService {

    private static final Logger logger = LogManager.getLogger(TokenService.class);

    private final UserService userService;

    private final TokenRepository tokenRepository;

    private final Clock clock;

    @Autowired
    public TokenService(@Lazy UserService userService, TokenRepository tokenRepository, Clock clock) {
        this.userService = userService;
        this.tokenRepository = tokenRepository;
        this.clock = clock;
    }

    @Transactional
    public void addToken(VerificationToken token) {
        logger.info("addToken was called: {}", token.getApplicationUser().getUsername());
        tokenRepository.save(token);
    }

    public Optional<VerificationToken> getToken(String token) {
        logger.info("getToken was called: {}", token);
        return tokenRepository.findByToken(token);
    }

    @Transactional
    public void confirmToken(String token) {
        logger.info("confirmToken was called: {}", token);

        VerificationToken verificationToken = getToken(token).orElseThrow(() -> {
            IllegalStateException illegalStateException = new IllegalStateException(String.format("Token %s not found", token));
            logger.error("error in confirmToken: {}", token, illegalStateException);
            return illegalStateException;
        });

        if (verificationToken.getConfirmedAt() != null) {
            IllegalStateException illegalStateException = new IllegalStateException("Email already confirmed");
            logger.error("error in confirmToken: {}", token, illegalStateException);
            throw illegalStateException;
        }

        LocalDateTime expiredAt = verificationToken.getExpired();
        if (expiredAt.isBefore(LocalDateTime.now(clock))) {
            IllegalStateException illegalStateException = new IllegalStateException(String.format("Token %s expired", token));
            logger.error("error in confirmToken: {}", token, illegalStateException);
            throw illegalStateException;
        }

        setConfirmedAt(token);
        if (!verificationToken.getApplicationUser().getEnabled()) {
            userService.enableAppUser(verificationToken.getApplicationUser().getEmail());
        }
    }

    @Transactional
    public void setConfirmedAt(String token) {
        logger.info("setConfirmedAt was called: {}", LocalDateTime.now());
        tokenRepository.updateConfirmedAt(token, LocalDateTime.now(clock));
    }

    @Transactional
    public VerificationToken generateVerificationToken(ApplicationUser applicationUser) {
        logger.info("generateVerificationToken was called: {}", applicationUser.getUsername());
        String randomToken = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(
                randomToken,
                LocalDateTime.now(clock),
                LocalDateTime.now(clock).plusMinutes(20L),
                applicationUser
        );
        addToken(verificationToken);
        return verificationToken;
    }
}
