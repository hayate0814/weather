package com.jp.kinto.app.bff.core.exception;

import org.springframework.http.HttpStatus;

public class AssertFailException extends BffException {
  private static final String PREFIX = "[Assertion failed] - ";

  public AssertFailException(String message) {
    super(HttpStatus.INTERNAL_SERVER_ERROR, PREFIX + message);
  }
}
