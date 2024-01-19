package com.jp.kinto.app.bff.goku.client;

import com.jp.kinto.app.bff.core.exception.JpIdApiMessageException;
import com.jp.kinto.app.bff.core.exception.RemoteApiException;
import com.jp.kinto.app.bff.core.exception.UnauthorizedException;
import com.jp.kinto.app.bff.goku.response.WithCookie;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class JpIdClient extends AbstractClient {

  public JpIdClient(WebClient webClient) {
    super(webClient);
  }

  public <T extends WithCookie> Mono<T> callWithCookie(
      Class<T> responseClass, Function<WebClient, WebClient.RequestHeadersSpec<?>> requestHandler) {
    return requestHandler
        .apply(webClient)
        .exchangeToMono(this.responseHandler(resp -> resp.bodyToMono(responseClass)))
        .doOnError(IOException.class, onError);
  }

  protected <T> Function<ClientResponse, Mono<T>> responseHandler(
      Function<ClientResponse, Mono<T>> successFun) {
    return resp -> {
      HttpStatusCode httpStatusCode = resp.statusCode();
      Optional<MediaType> contentType = resp.headers().contentType();
      // jp-idに合わせる
      if (httpStatusCode.is2xxSuccessful()) {
        return successFun
            .apply(resp)
            .map(
                obj -> {
                  log.debug("obj:{}:{}", obj.getClass().getName(), obj instanceof WithCookie);
                  if (obj instanceof WithCookie) {
                    ((WithCookie) obj).setCookies(resp.cookies());
                  }
                  return obj;
                });
      } else if (httpStatusCode == HttpStatus.FORBIDDEN) {
        // CloudFrontから、403の場合
        throw new RemoteApiException(
            HttpStatus.FORBIDDEN, "jp-id 403 Forbidden error", getHttpRequest(resp));
      } else if (httpStatusCode == HttpStatus.UNAUTHORIZED) {
        // 401の場合
        throw UnauthorizedException.MEMBER_UNAUTHORIZED;
      } else if (httpStatusCode == HttpStatus.NOT_FOUND
          && contentType.isPresent()
          && !contentType.get().equals(MediaType.APPLICATION_JSON)
          && !contentType.get().equals(MediaType.APPLICATION_PROBLEM_JSON)) {
        // Jp-IDから、404の場合
        throw new RemoteApiException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "jp-id 404 error",
            httpStatusCode.value(),
            getHttpRequest(resp));
      } else if (httpStatusCode == HttpStatus.BAD_REQUEST) {
        return resp.bodyToMono(Map.class)
            .map(
                content -> {
                  // Jp-IDに合わせる
                  throw new JpIdApiMessageException(
                      resp.statusCode().value(), getHttpRequest(resp), content);
                });
      } else if (contentType.isPresent()
          && (contentType.get().equals(MediaType.APPLICATION_JSON)
              || contentType.get().equals(MediaType.APPLICATION_PROBLEM_JSON))) {
        // jp-id からメッセージ. content-type: application/problem+json, application/json
        return resp.bodyToMono(Map.class)
            .map(
                content -> {
                  // jp-id に合わせる
                  throw new JpIdApiMessageException(
                      resp.statusCode().value(), getHttpRequest(resp), content);
                });
      } else {
        // unknown error
        throw new RemoteApiException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "jp-id unknown error",
            httpStatusCode.value(),
            getHttpRequest(resp));
      }
    };
  }
}
