package com.jp.kinto.app.bff.core.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BffException {

  private final Integer code;
  public static final UnauthorizedException BFF_TOKEN_UNAUTHORIZED =
      new UnauthorizedException(1, "トークンが無効です。");
  public static final UnauthorizedException MEMBER_UNAUTHORIZED =
      new UnauthorizedException(2, "ログイン状態が無効です。");

  private UnauthorizedException(Integer code, String message) {
    super(HttpStatus.UNAUTHORIZED, message);
    this.code = code;
    this.logLevel = LogLevel.Debug;
  }

  protected Object intoResponse() {
    return new Body(code, getMessage());
  }
}
