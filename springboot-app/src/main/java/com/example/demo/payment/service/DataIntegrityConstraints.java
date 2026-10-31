package com.example.demo.payment.service;

import org.hibernate.exception.ConstraintViolationException;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class DataIntegrityConstraints {

    static final String IDEMPOTENCY_KEY = "IDEMPOTENCY_KEY";
    static final String REQUEST_NO = "REQUEST_NO";

    private static final Pattern QUOTED_KEY = Pattern.compile("(?i)for key\\s+'([^']+)'");

    private DataIntegrityConstraints() {
    }

    static boolean violates(Throwable exception, String token) {
        Throwable current = exception;
        while (current != null) {
            if (current instanceof ConstraintViolationException violation
                    && isConstraintName(violation.getConstraintName(), token)) {
                return true;
            }
            String message = current.getMessage();
            if (message != null && matches(QUOTED_KEY, message, token)) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private static boolean matches(Pattern pattern, String message, String token) {
        Matcher matcher = pattern.matcher(message);
        return matcher.find() && contains(matcher.group(1), token);
    }

    private static boolean isConstraintName(String constraintName, String token) {
        if (constraintName == null || constraintName.isBlank()) {
            return false;
        }
        if (constraintName.chars().anyMatch(Character::isWhitespace)) {
            return false;
        }
        return contains(constraintName, token);
    }

    private static boolean contains(String value, String token) {
        return value.toUpperCase(Locale.ROOT).contains(token);
    }
}
