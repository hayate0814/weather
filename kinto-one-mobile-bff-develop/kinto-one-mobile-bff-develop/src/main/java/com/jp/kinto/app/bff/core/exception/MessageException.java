package com.jp.kinto.app.bff.core.exception;

import java.util.HashMap;

/** エラーメッセージResponse */
public class MessageException extends BadRequestException {

  public MessageException(String message) {
    super(ErrorCode.InputError, message);
    logLevel = LogLevel.Debug;
  }

  @Override
  protected String getLogContent() {
    return "";
  }

  @Override
  protected Object intoResponse() {
    return new HashMap<String, Object>() {
      {
        put("code", code.getValue());
        put("message", getMessage());
      }
    };
  }
}
