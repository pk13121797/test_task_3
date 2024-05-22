package by.pavvel.exception;

public class PasswordsNotEqualException extends RuntimeException {

    public PasswordsNotEqualException() {
    }

    public PasswordsNotEqualException(String message) {
        super(message);
    }
}
