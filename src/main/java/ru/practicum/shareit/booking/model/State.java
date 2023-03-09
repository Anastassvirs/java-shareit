package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.exceptions.WrongParametersException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED,
    NOT_SUPPORTED;

    public static State validateState(String text) {
        try {
            return State.valueOf(text);
        } catch (Exception e) {
            throw new WrongParametersException("Unknown state: " + text);
        }
    }
}
