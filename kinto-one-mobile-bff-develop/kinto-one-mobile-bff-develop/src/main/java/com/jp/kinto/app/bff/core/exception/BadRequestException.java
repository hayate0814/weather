package com.jp.kinto.app.bff.core.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BffException {

  protected ErrorCode code;

  public BadRequestException(ErrorCode code, String message) {
    this(code, message, null);
  }

  public BadRequestException(ErrorCode code, String message, Throwable e) {
    this(code, message, e, LogLevel.Info);
  }

  public BadRequestException(ErrorCode code, String message, Throwable e, LogLevel logLevel) {
    super(HttpStatus.BAD_REQUEST, message, e);
    this.code = code;
    this.logLevel = logLevel;
  }

  @Override
  protected Object intoResponse() {
    return new Body(code.value, getMessage());
  }

  public static enum ErrorCode {
    /** リクエスト入力エラー */
    InputError(1),
    /** ロジックエラー */
    LogicMessage(2),
    /** 車両データ期限切れエラー */
    VehicleDataExpired(3);

    private final Integer value;

    public Integer getValue() {
      return this.value;
    }

    ErrorCode(Integer code) {
      this.value = code;
    }
  }
}
