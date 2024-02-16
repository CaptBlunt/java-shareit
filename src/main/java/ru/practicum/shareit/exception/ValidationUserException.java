package ru.practicum.shareit.exception;

public class ValidationUserException extends RuntimeException {

    public ValidationUserException(String message) {
        super(message);
    }
}