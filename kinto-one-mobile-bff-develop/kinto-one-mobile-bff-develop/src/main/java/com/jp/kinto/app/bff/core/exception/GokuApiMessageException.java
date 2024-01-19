package com.jp.kinto.app.bff.core.exception;

import static com.jp.kinto.app.bff.core.constant.Constant.API_URL_PREFIX;
import static com.jp.kinto.app.bff.core.constant.Constant.GOKU_API_VERSION;
import static com.jp.kinto.app.bff.core.exception.BadRequestException.ErrorCode;
import static com.jp.kinto.app.bff.core.message.Msg.VehicleDataExpired;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;

public class GokuApiMessageException extends RemoteApiMessageException {
  public static final String GOKU_ERROR_CODE_ENTITY_NOT_FOUND = "C006";
  private final String remoteErrorCode;
  private final String remoteMessage;

  public GokuApiMessageException(
      int remoteHttpStatus, Optional<HttpRequest> request, Map<?, ?> responseContent) {
    super(remoteHttpStatus, "goku api error.", request, responseContent);
    var errorCode = responseContent.get("errorCode");
    this.remoteErrorCode = errorCode == null ? "" : String.valueOf(errorCode);
    var message = responseContent.get("message");
    this.remoteMessage = message == null ? "" : String.valueOf(message);
  }

  @Override
  protected Object intoResponse() {
    String uri = remoteRequest.map(req -> req.getURI().toString()).orElse("");
    // gokuに合わせる
    status = HttpStatus.BAD_REQUEST;
    Body rs =
        switch (remoteErrorCode) {
          case GOKU_ERROR_CODE_ENTITY_NOT_FOUND -> { // C006
            if (uri.contains(API_URL_PREFIX + "/price/" + GOKU_API_VERSION)) {
              // 車両マスタデータが更新されました
              logLevel = LogLevel.Warn;
              yield new Body(ErrorCode.VehicleDataExpired.getValue(), VehicleDataExpired);
            } else if (uri.contains(API_URL_PREFIX + "/dealers/" + GOKU_API_VERSION)) {
              // 販売店検索時にデータ無しの場合
              logLevel = LogLevel.Debug;
            }
            yield new Body(ErrorCode.InputError.getValue(), remoteMessage);
          }
          default -> new Body(ErrorCode.InputError.getValue(), remoteMessage);
        };

    return new HashMap<>() {
      {
        put("code", rs.getCode());
        put("message", rs.getMessage());
      }
    };
  }

  public String getRemoteErrorCode() {
    return remoteErrorCode;
  }

  public String getRemoteMessage() {
    return remoteMessage;
  }
}
