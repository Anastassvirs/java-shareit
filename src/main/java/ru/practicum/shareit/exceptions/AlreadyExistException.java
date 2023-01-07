package ru.practicum.shareit.exceptions; // Вопрос в том, нормально ли в такой структуре папок оставлять папку с ошибками

public class AlreadyExistException extends RuntimeException {
    public AlreadyExistException(final String message) {
        super(message);
    }
}