package com.jovanovicbogdan.auticparkic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
public class NotImplementedException extends RuntimeException {

  public NotImplementedException(final String message) {
    super(message);
  }
}
