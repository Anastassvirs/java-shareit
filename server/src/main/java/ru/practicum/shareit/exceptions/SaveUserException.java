package ru.practicum.shareit.exceptions;

public class SaveUserException extends RuntimeException {
    public SaveUserException(final String message) {
        super(message);
    }
}
