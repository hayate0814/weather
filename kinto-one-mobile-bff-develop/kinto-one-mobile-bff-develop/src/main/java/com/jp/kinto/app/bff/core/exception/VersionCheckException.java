package com.jp.kinto.app.bff.core.exception;

import java.util.HashMap;
import org.springframework.http.HttpStatus;

public class VersionCheckException extends BffException {
  private boolean require;
  private String version;

  public VersionCheckException(boolean require, String version) {
    super(HttpStatus.UPGRADE_REQUIRED, HttpStatus.UPGRADE_REQUIRED.getReasonPhrase());
    this.logLevel = LogLevel.Debug;
    this.require = require;
    this.version = version;
  }

  @Override
  protected Object intoResponse() {
    return new HashMap<>() {
      {
        put("require", require);
        put("version", version);
      }
    };
  }
}
