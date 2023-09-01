package com.jovanovicbogdan.auticparkic.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;

@ControllerAdvice
public class DefaultExceptionHandler {

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiResponse> handleException(final BadRequestException ex,
      final HttpServletRequest request) {
    final ApiResponse response = new ApiResponse(request.getRequestURI(), ex.getMessage(),
        HttpStatus.BAD_REQUEST.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse> handleException(final ResourceNotFoundException ex,
      final HttpServletRequest request) {
    final ApiResponse response = new ApiResponse(request.getRequestURI(), ex.getMessage(),
        HttpStatus.NOT_FOUND.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponse> handleException(final MethodArgumentTypeMismatchException ex,
      final HttpServletRequest request) {
    final ApiResponse response = new ApiResponse(request.getRequestURI(),
        "Invalid parameter(s)", HttpStatus.BAD_REQUEST.value(),
        LocalDateTime.now());
    ex.printStackTrace();
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResponse> handleException(
      final MissingServletRequestParameterException ex,
      final HttpServletRequest request) {
    final ApiResponse response = new ApiResponse(request.getRequestURI(),
        "Missing request parameter", HttpStatus.BAD_REQUEST.value(),
        LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse> handleException(final ConstraintViolationException ex,
      final HttpServletRequest request) {
    final ApiResponse response = new ApiResponse(request.getRequestURI(),
        "Invalid parameter(s)", HttpStatus.BAD_REQUEST.value(),
        LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ApiResponse> handleException(final ConflictException ex,
      final HttpServletRequest request) {
    final ApiResponse response = new ApiResponse(request.getRequestURI(), ex.getMessage(),
        HttpStatus.CONFLICT.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(NotImplementedException.class)
  public ResponseEntity<ApiResponse> handleException(final NotImplementedException ex,
      final HttpServletRequest request) {
    final ApiResponse response = new ApiResponse(request.getRequestURI(), ex.getMessage(),
        HttpStatus.NOT_IMPLEMENTED.value(), LocalDateTime.now());
    return new ResponseEntity<>(response, HttpStatus.NOT_IMPLEMENTED);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse> handleException(final MethodArgumentNotValidException ex,
      final HttpServletRequest request) {
    final ApiResponse response = new ApiResponse(request.getRequestURI(),
        "Invalid parameter(s)", HttpStatus.BAD_REQUEST.value(),
        LocalDateTime.now());
    ex.printStackTrace();
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse> handleException(final HttpMessageNotReadableException ex,
      final HttpServletRequest request) {
    final ApiResponse response = new ApiResponse(request.getRequestURI(),
        "Invalid parameter(s)", HttpStatus.BAD_REQUEST.value(),
        LocalDateTime.now());
    ex.printStackTrace();
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse> handleException(final Exception ex,
      final HttpServletRequest request) {
    final ApiResponse response = new ApiResponse(request.getRequestURI(), null,
        HttpStatus.INTERNAL_SERVER_ERROR.value(), LocalDateTime.now());
    ex.printStackTrace();
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
