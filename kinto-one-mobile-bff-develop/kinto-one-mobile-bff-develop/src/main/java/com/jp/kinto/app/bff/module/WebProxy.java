package com.jp.kinto.app.bff.module;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import com.jp.kinto.app.bff.core.exception.JpIdApiMessageException;
import com.jp.kinto.app.bff.core.exception.RemoteApiException;
import com.jp.kinto.app.bff.core.properties.GokuProperties;
import com.jp.kinto.app.bff.goku.client.AbstractClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class WebProxy extends AbstractClient {
  //  private WebClient webClient;

  public WebProxy(Function<String, WebClient> webClientFun, GokuProperties gokuProperties) {
    //    webClient = webClientFun.apply(gokuProperties.getUrl());
    super(webClientFun.apply(gokuProperties.getUrl()));
  }

  public Flux<DataBuffer> get(ServerWebExchange exchange) {
    return get(exchange, CollectionUtils.toMultiValueMap(new HashMap<>()));
  }

  public Flux<DataBuffer> get(
      ServerWebExchange exchange, MultiValueMap<String, ? extends String> extraHeaders) {
    return get(exchange.getRequest(), exchange.getResponse(), extraHeaders);
  }

  public Flux<DataBuffer> get(
      ServerHttpRequest request,
      ServerHttpResponse response,
      MultiValueMap<String, ? extends String> extraHeaders) {
    log.debug("request path:" + request.getPath().value());
    log.debug("request query-params:" + request.getQueryParams());
    extraHeaders.forEach(
        (k, v) -> {
          log.debug("request header:" + k + "=" + v);
        });
    return webClient
        .get()
        .uri(
            builder ->
                builder
                    .path(request.getPath().value())
                    .queryParams(request.getQueryParams())
                    .build())
        .headers(
            headers -> {
              extraHeaders.forEach(headers::addAll);
            })
        .exchangeToFlux(this.responseHandler(response))
        .doOnError(IOException.class, onError);
  }

  private Function<ClientResponse, ? extends Flux<DataBuffer>> responseHandler(
      ServerHttpResponse response) {
    return resp -> {
      HttpStatusCode httpStatusCode = resp.statusCode();
      // gokuに合わせる  HttpStatus.ACCEPTED(202): 商流が同じで変更する必要がない場合のエラーハンドリング
      if (httpStatusCode.is2xxSuccessful() && httpStatusCode != HttpStatus.ACCEPTED) {
        response.setStatusCode(resp.statusCode());
        resp.headers()
            .asHttpHeaders()
            .forEach(
                (name, values) -> {
                  log.debug("response header:" + name + "=" + values);
                  if (CONTENT_TYPE.equalsIgnoreCase(name)) {
                    // remove default content-type: text/event-stream
                    response.getHeaders().set(name, values.get(0));
                  } else {
                    response.getHeaders().addAll(name, values);
                  }
                });
        return resp.bodyToFlux(DataBuffer.class);
      } else if (httpStatusCode == HttpStatus.FORBIDDEN) {
        // TODO CloudFrontから、403の場合
        throw new RemoteApiException(
            HttpStatus.FORBIDDEN, "403 Forbidden エラー", getHttpRequest(resp));
      } else {
        return resp.bodyToFlux(Map.class)
            .map(
                map -> {
                  // gokuに合わせる
                  throw new JpIdApiMessageException(
                      resp.statusCode().value(), getHttpRequest(resp), map);
                });
      }
    };
  }

  @Override
  protected <T> Function<ClientResponse, Mono<T>> responseHandler(
      Function<ClientResponse, Mono<T>> successFun) {
    return null;
  }
}
