package com.hammaad.codecanvas.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Consistent JSON errors for the REST API: {"status": 400, "error": "..."}.
 * Expected client errors return a short safe message; unexpected failures are logged with a
 * reference id and the client only sees that id (no stack traces or internal details).
 */
@RestControllerAdvice(annotations = RestController.class)
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleStatus(ResponseStatusException e) {
        String reason = e.getReason() != null ? e.getReason() : defaultMessage(e.getStatusCode());
        return body(e.getStatusCode(), reason, null);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception e) {
        return body(HttpStatus.BAD_REQUEST, "Malformed request", null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethod(HttpRequestMethodNotSupportedException e) {
        return body(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed", null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMediaType(HttpMediaTypeNotSupportedException e) {
        return body(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Content type must be application/json", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception e) {
        String reference = UUID.randomUUID().toString();
        log.error("Unhandled API error reference={}", reference, e);
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again.", reference);
    }

    private static String defaultMessage(HttpStatusCode status) {
        HttpStatus resolved = HttpStatus.resolve(status.value());
        return resolved != null ? resolved.getReasonPhrase() : "Error";
    }

    private static ResponseEntity<Map<String, Object>> body(HttpStatusCode status, String message, String reference) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("error", message);
        if (reference != null) {
            body.put("reference", reference);
        }
        return ResponseEntity.status(status).body(body);
    }
}
