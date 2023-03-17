package ru.practicum.shareit.exceptions;

public class WrongParametersException extends RuntimeException {
    public WrongParametersException(final String message) {
        super(message);
    }
}
