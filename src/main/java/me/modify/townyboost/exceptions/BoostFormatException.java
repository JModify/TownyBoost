package me.modify.townyboost.exceptions;

/**
 * Exception thrown when an encoded booster string attempts to be read and is of invalid format.
 */
public class BoostFormatException extends Exception {

    public BoostFormatException() {
        super();
    }

    public BoostFormatException(String message) {
        super(message);
    }

}
