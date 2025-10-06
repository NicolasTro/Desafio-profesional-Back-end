package com.backend.cards_service.exception;

public class CardAlreadyExistsInAccountException extends RuntimeException {
    public CardAlreadyExistsInAccountException(String message) {
        super(message);
    }
}
