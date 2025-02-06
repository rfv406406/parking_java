package com.sideproject.parking_java.Exception;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class StatusCode {

    @AllArgsConstructor
    @Getter
    public enum Base {
        INTERNAL_SERVER_ERROR("E9999"),
        INVALID_PARAMETER("E9901"),
        S3_ERROR("E9904"),
        DATABASE_ERROR("E9902"),
        AUTHENTICATION_ERROR("E9905");

        @JsonValue
        private final String status;
    }
}
