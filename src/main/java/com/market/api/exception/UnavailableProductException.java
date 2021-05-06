package com.market.api.exception;

public class UnavailableProductException extends RuntimeException{
    public UnavailableProductException(String message) {
        super(message);
    }
}
