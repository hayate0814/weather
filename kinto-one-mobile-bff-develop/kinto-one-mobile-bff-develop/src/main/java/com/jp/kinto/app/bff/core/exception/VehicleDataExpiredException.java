package com.jp.kinto.app.bff.core.exception;

import com.jp.kinto.app.bff.core.message.Msg;

public class VehicleDataExpiredException extends BadRequestException {
  public VehicleDataExpiredException() {
    super(ErrorCode.VehicleDataExpired, Msg.VehicleDataExpired);
    this.logLevel = LogLevel.Warn;
  }
}
