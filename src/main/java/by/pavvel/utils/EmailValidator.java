package by.pavvel.utils;

import org.springframework.stereotype.Component;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmailValidator implements Predicate<String> {

    private final Pattern pattern;

    private static final String EMAIL_PATTERN = "^([a-z\\d.-]+)@([a-z\\d-]+)\\.([a-z]{2,8})(\\.[a-z]{2,8})?$";

    public EmailValidator() {
        pattern = Pattern.compile(EMAIL_PATTERN);
    }

    @Override
    public boolean test(String s) {
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }
}
