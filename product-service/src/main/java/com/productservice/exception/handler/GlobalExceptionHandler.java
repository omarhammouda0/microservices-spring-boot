package com.productservice.exception.handler;



import com.productservice.exception.base.AppException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleInvalidInput(HttpMessageNotReadableException ex , WebRequest request) {

        String message = ex.getMessage ( );
        String errorDetail;
        String errorCode;

        if (message != null) {


            if (message.contains ( "not one of the values accepted for Enum class" )) {

                errorCode = "INVALID_ENUM_VALUE";
                errorDetail = "Invalid value provided for an enum field. Please check the allowed values for your input.";

            } else if (message.contains ( "Cannot deserialize value of type" ) &&
                    (message.contains ( "Instant" ) || message.contains ( "LocalDate" ) || message.contains ( "LocalDateTime" ))) {

                errorCode = "INVALID_DATE_FORMAT";
                errorDetail = "Invalid date/time format. Please use ISO-8601 format (e.g., 2025-11-26T00:16:25.430Z).";

            } else if (message.contains ( "not a valid" ) && (message.contains ( "Integer" ) || message.contains ( "Long" ))) {

                errorCode = "INVALID_NUMBER_FORMAT";
                errorDetail = "Invalid number format. Please provide a valid numeric value.";

            } else if (message.contains ( "required" ) || message.contains ( "missing" )) {

                errorCode = "MISSING_REQUIRED_FIELD";
                errorDetail = "Required field is missing. Please check your request body.";

            } else if (message.contains ( "Unexpected character" ) || message.contains ( "JSON parse error" )) {

                errorCode = "INVALID_JSON_SYNTAX";
                errorDetail = "Invalid JSON format. Please check your request syntax.";

            } else {

                errorCode = "INVALID_REQUEST_FORMAT";
                errorDetail = "Invalid request format. Please check your input data.";
            }

        } else {

            errorCode = "INVALID_REQUEST_FORMAT";
            errorDetail = "Invalid request format. Please check your input data.";
        }

        log.error ( "Invalid input: {}" , errorDetail );

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail (
                HttpStatus.BAD_REQUEST ,
                errorDetail
        );
        problemDetail.setTitle ( "Invalid Request" );
        problemDetail.setProperty ( "code" , errorCode );
        problemDetail.setProperty ( "timestamp" , Instant.now ( ) );
        problemDetail.setProperty ( "path" , request.getDescription ( false ).replace ( "uri=" , "" ) );

        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex , WebRequest request) {
        log.error ( "Validation failed: {}" , ex.getMessage ( ) );

        Map<String, String> errors = new HashMap<> ( );
        ex.getBindingResult ( ).getAllErrors ( ).forEach ( error -> {
            String fieldName = ((FieldError) error).getField ( );
            String errorMessage = error.getDefaultMessage ( );
            errors.put ( fieldName , errorMessage );
        } );

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail (
                HttpStatus.BAD_REQUEST ,
                "Validation failed for one or more fields"
        );
        problemDetail.setTitle ( "Validation Error" );
        problemDetail.setProperty ( "errors" , errors );
        problemDetail.setProperty ( "code" , "VALIDATION_ERROR" );
        problemDetail.setProperty ( "timestamp" , Instant.now ( ) );
        problemDetail.setProperty ( "path" , request.getDescription ( false ).replace ( "uri=" , "" ) );

        return problemDetail;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex , WebRequest request) {
        log.error ( "Constraint violation: {}" , ex.getMessage ( ) );

        Map<String, String> errors = ex.getConstraintViolations ( ).stream ( )
                .collect ( Collectors.toMap (
                        violation -> violation.getPropertyPath ( ).toString ( ) ,
                        ConstraintViolation::getMessage
                ) );

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail (
                HttpStatus.BAD_REQUEST ,
                "Constraint violation occurred"
        );
        problemDetail.setTitle ( "Validation Error" );
        problemDetail.setProperty ( "errors" , errors );
        problemDetail.setProperty ( "code" , "CONSTRAINT_VIOLATION" );
        problemDetail.setProperty ( "timestamp" , Instant.now ( ) );
        problemDetail.setProperty ( "path" , request.getDescription ( false ).replace ( "uri=" , "" ) );

        return problemDetail;
    }

    @ExceptionHandler(AppException.class)
    public ProblemDetail handleAppException(AppException ex , WebRequest request) {
        log.error ( "Application exception: {}" , ex.getMessage ( ) );

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail (
                ex.getStatus ( ) ,
                ex.getMessage ( )
        );

        problemDetail.setTitle ( ex.getClass ( ).getSimpleName ( ) );
        problemDetail.setProperty ( "code" , ex.getCode ( ) );
        problemDetail.setProperty ( "timestamp" , Instant.now ( ) );
        problemDetail.setProperty ( "path" , request.getDescription ( false ).replace ( "uri=" , "" ) );

        return problemDetail;
    }

    @ExceptionHandler (IllegalStateException.class)
    public ProblemDetail handleIllegalStateException(IllegalStateException ex , WebRequest request) {

        log.error ( "Illegal state exception: {}" , ex.getMessage ( ) );

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail (
                HttpStatus.BAD_REQUEST ,
                ex.getMessage ( )
        );

        problemDetail.setTitle ( "IllegalStateException" );
        problemDetail.setProperty ( "code" , "ILLEGAL_STATE" );
        problemDetail.setProperty ( "timestamp" , Instant.now ( ) );
        problemDetail.setProperty ( "path" , request.getDescription ( false ).replace ( "uri=" , "" ) );

        return problemDetail;
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ProblemDetail handleOptimisticLockException(
            ObjectOptimisticLockingFailureException ex, WebRequest request) {

        log.warn("Optimistic lock conflict: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "Resource was modified by another request, please retry"
        );
        problemDetail.setTitle("Conflict");
        problemDetail.setProperty("code", "OPTIMISTIC_LOCK_CONFLICT");
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("path", request.getDescription(false).replace("uri=", ""));

        return problemDetail;
    }

}