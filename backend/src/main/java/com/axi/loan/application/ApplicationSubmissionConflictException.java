package com.axi.loan.application;

public class ApplicationSubmissionConflictException extends RuntimeException {

    private final String field;

    public ApplicationSubmissionConflictException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
