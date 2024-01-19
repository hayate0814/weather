package com.jp.kinto.app.bff.core.exception;

import static com.jp.kinto.app.bff.core.message.Msg.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jp.kinto.app.bff.utils.Casts;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;

public abstract class RemoteApiMessageException extends BffException {
  protected static final ObjectMapper objectMapper = new ObjectMapper();

  protected final Map<?, ?> responseContent;
  protected Optional<HttpRequest> remoteRequest = Optional.empty();
  protected final HttpStatus remoteHttpStatus;

  public RemoteApiMessageException(
      int remoteHttpStatus,
      String exceptionMessage,
      Optional<HttpRequest> remoteRequest,
      Map<?, ?> responseContent) {
    super(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "remoteHttpStatus:" + remoteHttpStatus + ", " + exceptionMessage);
    this.remoteRequest = remoteRequest;
    this.responseContent = responseContent;
    this.remoteHttpStatus = HttpStatus.valueOf(remoteHttpStatus);
  }

  @Override
  protected String getLogContent() {
    var url = remoteRequest.map(value -> value.getURI().toString()).orElse("unknown");
    var method =
        remoteRequest.map(httpRequest -> httpRequest.getMethod().toString()).orElse("unknown");
    return format(
        "request: {} {}; response status: {}, content: {}",
        method,
        url,
        remoteHttpStatus,
        Casts.toJson(objectMapper, responseContent));
  }

  @Override
  protected abstract Object intoResponse();

  public HttpStatus getRemoteHttpStatus() {
    return remoteHttpStatus;
  }
}
