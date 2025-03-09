package com.sideproject.parking_java.exception;

public class InternalServerError extends ServiceException{
    public InternalServerError(String message) {
        super(StatusCode.Base.INTERNAL_SERVER_ERROR, message);
    }
}
