package com.sideproject.parking_java.Exception;

import com.fasterxml.jackson.annotation.JsonValue;

public class StatusCode {

    public interface interfaceStatusCode {
        String getErrorName();
    };

    // @AllArgsConstructor
    // @Getter
    public enum Base implements interfaceStatusCode{
        INTERNAL_SERVER_ERROR("E9999"),
        INVALID_PARAMETER("E9901"),
        S3_ERROR("E9904"),
        DATABASE_ERROR("E9902"),
        AUTHENTICATION_ERROR("E9905");

        @JsonValue
        private final String status;

        private Base(String status) {
            this.status = status;
        }

        @Override
        public String getErrorName() {
            return this.name();
        }
    }
}
