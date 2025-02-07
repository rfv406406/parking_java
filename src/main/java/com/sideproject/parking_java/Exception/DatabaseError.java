package com.sideproject.parking_java.Exception;

public class DatabaseError extends ServiceException{
    public DatabaseError(String message) {
        super(StatusCode.Base.DATABASE_ERROR, message);
    }
}
