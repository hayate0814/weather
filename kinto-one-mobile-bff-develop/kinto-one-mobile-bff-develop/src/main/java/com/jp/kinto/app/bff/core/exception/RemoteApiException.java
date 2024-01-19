package com.jp.kinto.app.bff.core.exception;

import static com.jp.kinto.app.bff.core.message.Msg.format;

import java.util.Optional;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;

public class RemoteApiException extends BffException {

  protected Optional<HttpRequest> remoteRequest = Optional.empty();
  protected HttpStatus remoteHttpStatus;

  public RemoteApiException(HttpStatus status, String message, Throwable e) {
    super(status, message, e);
  }

  public RemoteApiException(
      HttpStatus status, String message, Optional<HttpRequest> remoteRequest) {
    this(status, message, status.value(), remoteRequest);
  }

  public RemoteApiException(
      HttpStatus status,
      String message,
      int remoteHttpStatus,
      Optional<HttpRequest> remoteRequest) {
    super(status, message);
    this.remoteHttpStatus = HttpStatus.valueOf(remoteHttpStatus);
    this.remoteRequest = remoteRequest;
  }

  @Override
  public LogLevel logLevel() {
    return LogLevel.Error;
  }

  @Override
  protected String getLogContent() {
    if (remoteRequest.isPresent()) {
      var url = remoteRequest.get().getURI().toString();
      var method = remoteRequest.get().getMethod().toString();
      return format("request: {} {}, response-status: {}", method, url, remoteHttpStatus);
    } else {
      return getMessage();
    }
  }
}
