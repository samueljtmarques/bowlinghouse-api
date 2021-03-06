package com.samuel.bowling.exception;

import lombok.Getter;

/**
 * Exception thrown when:
 * - there are too many player
 * - players names and number don't match
 */
@Getter
public class PlayerNotFoundException extends RuntimeException{
    public PlayerNotFoundException(String message) {
        super(message);
    }
}
