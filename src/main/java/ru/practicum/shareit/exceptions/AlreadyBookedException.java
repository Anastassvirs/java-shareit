package ru.practicum.shareit.exceptions;

public class AlreadyBookedException extends RuntimeException {
    public AlreadyBookedException(final String message) {
        super(message);
    }
}
