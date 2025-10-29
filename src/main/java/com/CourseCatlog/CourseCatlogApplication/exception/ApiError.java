package com.CourseCatlog.CourseCatlogApplication.exception;

import java.time.Instant;

public class ApiError {
    private final Instant timestamp = Instant.now();
    private final int status;
    private final String error;

    public ApiError(int status, String error) {
        this.status = status; this.error = error;
    }

    public Instant getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
}