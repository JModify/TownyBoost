package me.modify.townyboost.exceptions;

/**
 * Exception thrown when an encoded booster token string attempts to be read and is of invalid format.
 */
public class BoostTokenFormatException extends Exception {

    public BoostTokenFormatException() {
        super();
    }

    public BoostTokenFormatException(String message) {
        super(message);
    }

}
