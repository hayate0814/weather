package com.jp.kinto.app.bff.core.exception;

import static com.jp.kinto.app.bff.core.exception.BadRequestException.ErrorCode;
import static com.jp.kinto.app.bff.core.message.Msg.LoginMistake;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;

public class JpIdApiMessageException extends RemoteApiMessageException {

  public JpIdApiMessageException(
      int remoteHttpStatus, Optional<HttpRequest> request, Map<?, ?> responseContent) {
    super(remoteHttpStatus, "jp-id api error.", request, responseContent);
  }

  @Override
  protected Object intoResponse() {
    // jp-idに合わせる
    String errorCode = String.valueOf(responseContent.get("code"));
    String message = String.valueOf(responseContent.get("message"));
    Body rs =
        switch (errorCode) {
            // メールアドレス あるいは パスワード が異なります
          case "V001" -> {
            status = HttpStatus.BAD_REQUEST;
            logLevel = LogLevel.Debug;
            yield new Body(ErrorCode.LogicMessage.getValue(), LoginMistake);
          }
          default -> new Body(ErrorCode.InputError.getValue(), message);
        };

    return new HashMap<>() {
      {
        put("code", rs.getCode());
        put("message", rs.getMessage());
      }
    };
  }
}
