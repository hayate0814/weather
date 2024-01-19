package com.jp.kinto.app.bff.core.exception;

/** 例外Logの出力Level */
public interface ExceptionLogLevel {
  enum LogLevel {
    Error,
    Warn,
    Info,
    Debug
  }

  /**
   * get LogLevel
   *
   * @return LogLevel
   */
  default LogLevel logLevel() {
    return LogLevel.Error;
  }

  /**
   * set LogLevel
   *
   * @param logLevel LogLevel
   */
  void setLogLevel(LogLevel logLevel);
}
