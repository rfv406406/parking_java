package com.sideproject.parking_java.exception;

public class DatabaseError extends ServiceException{
    public DatabaseError(String message) {
        super(StatusCode.Base.DATABASE_ERROR, message);
    }
}
