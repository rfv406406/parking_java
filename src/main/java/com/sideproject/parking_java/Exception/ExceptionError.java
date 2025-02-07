package com.sideproject.parking_java.Exception;

public class ExceptionError extends ServiceException{
    public ExceptionError(String message) {
        super(StatusCode.Base.INTERNAL_SERVER_ERROR, message);
    }
}
