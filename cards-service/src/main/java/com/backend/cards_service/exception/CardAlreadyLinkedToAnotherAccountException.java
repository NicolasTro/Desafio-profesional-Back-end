package com.backend.cards_service.exception;

public class CardAlreadyLinkedToAnotherAccountException extends RuntimeException {
    public CardAlreadyLinkedToAnotherAccountException(String message) {
        super(message);
    }
}
