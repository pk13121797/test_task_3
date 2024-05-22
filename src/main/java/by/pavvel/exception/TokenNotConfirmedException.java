package by.pavvel.exception;

public class TokenNotConfirmedException extends RuntimeException {

    public TokenNotConfirmedException() {
    }

    public TokenNotConfirmedException(String message) {
        super(message);
    }
}
