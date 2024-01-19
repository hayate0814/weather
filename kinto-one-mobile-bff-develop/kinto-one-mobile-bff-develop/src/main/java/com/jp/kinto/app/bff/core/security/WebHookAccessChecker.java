package com.jp.kinto.app.bff.core.security;

import static com.jp.kinto.app.bff.core.constant.Constant.API_URL_PREFIX;

import com.jp.kinto.app.bff.core.properties.WebHookProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class WebHookAccessChecker implements ReactiveAuthorizationManager<AuthorizationContext> {

  @Autowired private WebHookProperties webHookProperties;

  @Override
  public Mono<AuthorizationDecision> check(
      Mono<Authentication> authentication, AuthorizationContext context) {
    ServerWebExchange exchange = context.getExchange();
    String requestPath = exchange.getRequest().getPath().toString();

    if (requestPath.equals(API_URL_PREFIX + "/notice") && hasAccessKey(exchange)) {
      return Mono.just(new AuthorizationDecision(true));
    }

    return Mono.just(new AuthorizationDecision(false));
  }

  private boolean hasAccessKey(ServerWebExchange exchange) {
    String providedAccessKey = exchange.getRequest().getHeaders().getFirst("mobile-api-access-key");
    if (providedAccessKey == null) {
      providedAccessKey = exchange.getRequest().getHeaders().getFirst("x-api-access-key");
    }
    return webHookProperties.getMobileApiAccessKey().equals(providedAccessKey);
  }
}
