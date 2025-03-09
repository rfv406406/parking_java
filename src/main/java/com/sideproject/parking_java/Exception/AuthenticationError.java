package com.sideproject.parking_java.exception;

public class AuthenticationError extends ServiceException{
    public AuthenticationError(String message) {
        super(StatusCode.Base.AUTHENTICATION_ERROR, message);
    }
}
