package com.jp.kinto.app.bff.goku.client;

import com.jp.kinto.app.bff.core.exception.RemoteApiException;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public abstract class AbstractClient {
  protected static final Logger log = LoggerFactory.getLogger("bff.remote.api");

  protected WebClient webClient;

  public AbstractClient(WebClient webClient) {
    this.webClient = webClient;
  }

  public <T> Mono<T> call(
      Class<T> responseClass, Function<WebClient, WebClient.RequestHeadersSpec<?>> requestHandler) {
    return requestHandler
        .apply(webClient)
        .exchangeToMono(this.responseHandler(responseClass))
        .doOnError(IOException.class, onError);
  }

  public <T> Mono<T> call(
      ParameterizedTypeReference<T> typeReference,
      Function<WebClient, WebClient.RequestHeadersSpec<?>> requestHandler) {
    return requestHandler
        .apply(webClient)
        .exchangeToMono(this.responseHandler(typeReference))
        .doOnError(IOException.class, onError);
  }

  private <T> Function<ClientResponse, Mono<T>> responseHandler(
      ParameterizedTypeReference<T> elementTypeRef) {
    return responseHandler(resp -> resp.bodyToMono(elementTypeRef));
  }

  private <T> Function<ClientResponse, Mono<T>> responseHandler(Class<T> clazz) {
    return responseHandler(resp -> resp.bodyToMono(clazz));
  }

  protected final Consumer<? super IOException> onError =
      err -> {
        throw new RemoteApiException(HttpStatus.INTERNAL_SERVER_ERROR, "リモート通信エラー", err);
      };

  protected abstract <T> Function<ClientResponse, Mono<T>> responseHandler(
      Function<ClientResponse, Mono<T>> successFun);

  @SuppressWarnings("unchecked")
  protected final Optional<HttpRequest> getHttpRequest(ClientResponse resp) {
    try {
      return Optional.ofNullable(ReflectionUtils.findField(resp.getClass(), "requestSupplier"))
          .map(
              f -> {
                ReflectionUtils.makeAccessible(f);
                return ((Supplier<HttpRequest>) ReflectionUtils.getField(f, resp)).get();
              });
    } catch (Exception e) {
      log.warn("failed get HttpRequest instance from class:{}", resp.getClass(), e);
      return Optional.empty();
    }
  }
}
