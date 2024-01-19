package com.jp.kinto.app.bff.goku.client;

import com.jp.kinto.app.bff.core.exception.*;
import java.util.Map;
import java.util.function.Function;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class CloudOcrClient extends AbstractClient {
  public CloudOcrClient(WebClient webClient) {
    super(webClient);
  }

  protected <T> Function<ClientResponse, Mono<T>> responseHandler(
      Function<ClientResponse, Mono<T>> successFun) {
    return resp -> {
      HttpStatusCode httpStatusCode = resp.statusCode();

      if (httpStatusCode.is2xxSuccessful() && httpStatusCode != HttpStatus.ACCEPTED) {
        return successFun.apply(resp);
      } else {
        // cloud ocrから、異常の場合
        return resp.bodyToMono(Map.class)
            .map(
                content -> {
                  throw new OCRApiMessageException(
                      resp.statusCode().value(), getHttpRequest(resp), content);
                });
      }
    };
  }
}
