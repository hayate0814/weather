package com.jp.kinto.app.bff.core.exception;

import static com.jp.kinto.app.bff.core.exception.BadRequestException.ErrorCode;
import static com.jp.kinto.app.bff.core.message.Msg.OcrGetTokenMistake;

import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;

public class OCRApiMessageException extends RemoteApiMessageException {

  public OCRApiMessageException(
      int remoteHttpStatus, Optional<HttpRequest> request, Map<?, ?> responseContent) {
    super(remoteHttpStatus, "ocr error", request, responseContent);
  }

  @Override
  protected Object intoResponse() {
    // ocrに合わせる
    status = HttpStatus.BAD_REQUEST;
    Body body = new Body(ErrorCode.LogicMessage.getValue(), OcrGetTokenMistake);
    return body;
  }
}
