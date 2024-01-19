package com.jp.kinto.app.bff.goku.client;

import com.jp.kinto.app.bff.core.exception.GokuApiMessageException;
import com.jp.kinto.app.bff.core.exception.RemoteApiException;
import com.jp.kinto.app.bff.core.exception.UnauthorizedException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GokuClient extends AbstractClient {

  public GokuClient(WebClient webClient) {
    super(webClient);
  }

  protected <T> Function<ClientResponse, Mono<T>> responseHandler(
      Function<ClientResponse, Mono<T>> successFun) {
    return resp -> {
      HttpStatusCode httpStatusCode = resp.statusCode();
      Optional<MediaType> contentType = resp.headers().contentType();
      // gokuに合わせる  HttpStatus.ACCEPTED(202): 商流が同じで変更する必要がない場合のエラーハンドリング
      if (httpStatusCode.is2xxSuccessful() && httpStatusCode != HttpStatus.ACCEPTED) {
        return successFun.apply(resp);
      } else if (httpStatusCode == HttpStatus.UNAUTHORIZED) {
        // gokuから、401の場合
        throw UnauthorizedException.MEMBER_UNAUTHORIZED;
      } else if (httpStatusCode == HttpStatus.NOT_FOUND
          && contentType.isPresent()
          && !contentType.get().equals(MediaType.APPLICATION_JSON)
          && !contentType.get().equals(MediaType.APPLICATION_PROBLEM_JSON)) {
        // gokuから、404の場合
        throw new RemoteApiException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "goku 404 error",
            httpStatusCode.value(),
            getHttpRequest(resp));
      } else if (httpStatusCode == HttpStatus.FORBIDDEN) {
        // CloudFrontから、403の場合
        throw new RemoteApiException(
            HttpStatus.FORBIDDEN, "goku 403 Forbidden error", getHttpRequest(resp));
      } else if (contentType.isPresent()
          && (contentType.get().equals(MediaType.APPLICATION_JSON)
              || contentType.get().equals(MediaType.APPLICATION_PROBLEM_JSON))) {
        // gokuからメッセージ. content-type: application/problem+json, application/json
        return resp.bodyToMono(Map.class)
            .map(
                content -> {
                  throw new GokuApiMessageException(
                      resp.statusCode().value(), getHttpRequest(resp), content);
                });
      } else {
        // unknown error
        throw new RemoteApiException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "goku unknown error",
            httpStatusCode.value(),
            getHttpRequest(resp));
      }
    };
  }
}
