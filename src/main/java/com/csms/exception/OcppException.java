package com.csms.exception;

import lombok.Getter;

@Getter
public class OcppException extends RuntimeException {
    
    private final String errorCode;
    
    public OcppException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public OcppException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    // Common OCPP error codes
    public static class ErrorCodes {
        public static final String NOT_IMPLEMENTED = "NotImplemented";
        public static final String NOT_SUPPORTED = "NotSupported";
        public static final String INTERNAL_ERROR = "InternalError";
        public static final String PROTOCOL_ERROR = "ProtocolError";
        public static final String SECURITY_ERROR = "SecurityError";
        public static final String FORMATION_VIOLATION = "FormationViolation";
        public static final String PROPERTY_CONSTRAINT_VIOLATION = "PropertyConstraintViolation";
        public static final String OCCURRENCE_CONSTRAINT_VIOLATION = "OccurrenceConstraintViolation";
        public static final String TYPE_CONSTRAINT_VIOLATION = "TypeConstraintViolation";
        public static final String GENERIC_ERROR = "GenericError";
    }
}