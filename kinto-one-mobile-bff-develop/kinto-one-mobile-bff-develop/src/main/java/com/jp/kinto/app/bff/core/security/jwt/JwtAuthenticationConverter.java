package com.jp.kinto.app.bff.core.security.jwt;

import com.jp.kinto.app.bff.core.exception.UnauthorizedException;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class JwtAuthenticationConverter
    implements Function<ServerWebExchange, Mono<Authentication>> {
  private static final String BEARER = "Bearer ";
  private final Predicate<String> matchBearerLength =
      authValue -> authValue.length() > BEARER.length();
  private final Function<String, String> isolateBearerValue =
      authValue -> authValue.substring(BEARER.length());

  @Autowired private JwtTokenProvider jwtTokenProvider;
  private final Function<ServerWebExchange, String> extract =
      serverWebExchange ->
          serverWebExchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

  private final Function<AuthUser, Authentication> token = JwtAuthenticationToken::new;

  @Override
  public Mono<Authentication> apply(ServerWebExchange serverWebExchange) {
    //    org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
    return Mono.justOrEmpty(serverWebExchange)
        .map(extract)
        .filter(matchBearerLength)
        .map(isolateBearerValue)
        .map(jwtTokenProvider::parseToken)
        .map(token)
        .onErrorResume(e -> Mono.error(UnauthorizedException.BFF_TOKEN_UNAUTHORIZED));
  }
}
