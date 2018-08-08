package com.samuel.bowling.exception;

import lombok.Getter;

/**
 * Exception thrown when game request isn't right
 */
@Getter
public class BadFrameRequestException extends RuntimeException{
    public BadFrameRequestException(String message) {
        super(message);
    }
}