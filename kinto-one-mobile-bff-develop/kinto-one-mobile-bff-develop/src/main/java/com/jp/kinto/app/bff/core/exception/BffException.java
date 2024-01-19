package com.jp.kinto.app.bff.core.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

public class BffException extends RuntimeException implements ExceptionLogLevel {

  protected LogLevel logLevel;

  protected HttpStatus status;

  public BffException(HttpStatus status, String message, Throwable e) {
    super(message, e);
    this.status = status;
    this.logLevel = LogLevel.Error;
  }

  public BffException(HttpStatus status, String message) {
    this(status, message, null);
  }

  public BffException(String message) {
    this(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
  }

  @Override
  public LogLevel logLevel() {
    return logLevel;
  }

  @Override
  public void setLogLevel(LogLevel logLevel) {
    this.logLevel = logLevel;
  }

  protected Object intoResponse() {
    return new Body(status.value(), getMessage());
  }

  protected String getLogContent() {
    return getMessage();
  }

  /**
   * logging
   *
   * @param logger Logger
   */
  protected void logging(org.slf4j.Logger logger) {
    var level = logLevel();
    level = level == null ? LogLevel.Error : level;
    switch (level) {
      case Warn -> logger.warn(getLogContent(), this);
      case Info -> logger.info(getLogContent(), this);
      case Debug -> logger.debug(getLogContent(), this);
      default -> logger.error(getLogContent(), this);
    }
  }

  @Data
  @AllArgsConstructor
  public static class Body {
    private Integer code;
    private String message;
  }
}
