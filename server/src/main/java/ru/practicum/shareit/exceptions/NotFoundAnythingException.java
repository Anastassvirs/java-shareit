package ru.practicum.shareit.exceptions;

public class NotFoundAnythingException extends RuntimeException {
    public NotFoundAnythingException(final String message) {
        super(message);
    }
}