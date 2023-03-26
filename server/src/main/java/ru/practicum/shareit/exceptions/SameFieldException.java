package ru.practicum.shareit.exceptions;

public class SameFieldException extends RuntimeException {
    public SameFieldException(final String message) {
        super(message);
    }
}
