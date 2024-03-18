package com.api.agendamentos.exceptions;

public class HttpMessageNotReadableException extends RuntimeException{

    public HttpMessageNotReadableException(String message) {
        super(message);
    }
}
