package com.itmo.secondspring.exception;

public class InvalidQueryParameterException extends RuntimeException {
    public InvalidQueryParameterException(String message) {
        super(message);
    }
}
