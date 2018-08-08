package com.samuel.bowling.exception;

import lombok.Getter;

/**
 * Exception thrown when game isn't started
 */
@Getter
public class GameNotStartedException extends RuntimeException {
    public GameNotStartedException(String message) {
        super(message);
    }
}
