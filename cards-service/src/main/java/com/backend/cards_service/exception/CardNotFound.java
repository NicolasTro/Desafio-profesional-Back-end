package com.backend.cards_service.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CardNotFound extends RuntimeException {
    public CardNotFound(String message) {
        super(message);
    }
}
