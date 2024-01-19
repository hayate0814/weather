package com.jp.kinto.app.bff.core.exception;

import static com.jp.kinto.app.bff.core.constant.Constant.INTERNAL_ERROR_CODE;

import com.jp.kinto.app.bff.core.exception.BffException.Body;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.MissingRequestValueException;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {

  private static final Logger slackLogger = LoggerFactory.getLogger("slack.notice.logger");

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<Object> handle(UnauthorizedException ex) {
    return new ResponseEntity<>(ex.intoResponse(), ex.status);
  }

  @ExceptionHandler(MissingRequestValueException.class)
  public ResponseEntity<Object> handle(MissingRequestValueException ex) {
    return new ResponseEntity<>(
        new HashMap<>() {
          {
            put("code", BadRequestException.ErrorCode.InputError.getValue());
            put("message", ex.getReason());
          }
        },
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(BffException.class)
  public ResponseEntity<Object> handle(BffException ex) {
    var body = ex.intoResponse();
    logBffException(ex);
    return new ResponseEntity<>(body, ex.status);
  }

  @ExceptionHandler(Throwable.class)
  public Object fatalErrorHandle(
      ServerHttpRequest request, ServerHttpResponse response, Throwable ex) {
    if (ex.getCause() != null && ex.getCause() instanceof BffException bffEx) {
      logBffException(bffEx);
    } else {
      slackLogger.error(ex.getMessage(), ex);
    }
    return new ResponseEntity<>(
        new Body(INTERNAL_ERROR_CODE, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private void logBffException(BffException bffEx) {
    // エラー記録
    if (slackLogger.isErrorEnabled() || slackLogger.isWarnEnabled()) {
      ExceptionLogLevel.LogLevel level =
          bffEx.logLevel() == null ? ExceptionLogLevel.LogLevel.Error : bffEx.logLevel();
      switch (level) {
        case Warn, Error -> bffEx.logging(slackLogger);
        default -> bffEx.logging(log);
      }
    } else {
      bffEx.logging(log);
    }
  }
}
