package com.jp.kinto.app.bff.core.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j(topic = "org.springframework.security")
public class BffErrorHandler implements ErrorWebExceptionHandler {
  private ObjectMapper objectMapper;

  public BffErrorHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
    //    DefaultErrorWebExceptionHandler
    var response = exchange.getResponse();
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

    byte[] messageBytes;
    if (ex instanceof BffException e) {
      log.error("BffException Error", ex);
      response.setStatusCode(e.status);
      if (Objects.isNull(e.intoResponse())) {
        // body is no exist
        return Mono.empty();
      }
      try {
        messageBytes = objectMapper.writeValueAsBytes(e.intoResponse());
      } catch (JsonProcessingException exc) {
        log.error("Failed to write object as json bytes ", exc);
        String msg =
            "{\"code\":999,\"message\":\"Failed to write exception-body as json bytes. exception:"
                + e.getClass().getName()
                + "\"}";
        messageBytes = msg.getBytes();
      }
    } else {
      log.error("Internal Server Error", ex);
      response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
      messageBytes = "{\"code\":999,\"message\":\"Internal Server Error\"}".getBytes();
    }
    var buffer = response.bufferFactory().wrap(messageBytes);
    return response.writeWith(Mono.just(buffer));
  }
}
