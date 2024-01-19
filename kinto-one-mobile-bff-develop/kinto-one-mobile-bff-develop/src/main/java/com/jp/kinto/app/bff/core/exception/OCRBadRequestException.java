package com.jp.kinto.app.bff.core.exception;

import com.jp.kinto.app.bff.core.message.Msg;

public class OCRBadRequestException extends BadRequestException {

  public OCRBadRequestException(Throwable e) {
    super(ErrorCode.LogicMessage, Msg.OcrGetTokenMistake, e, LogLevel.Error);
  }
}
