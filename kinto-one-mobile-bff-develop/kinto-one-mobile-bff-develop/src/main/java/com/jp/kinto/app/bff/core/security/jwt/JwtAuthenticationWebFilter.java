package com.jp.kinto.app.bff.core.security.jwt;

import static com.jp.kinto.app.bff.core.constant.Constant.API_URL_PREFIX;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.AndServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationWebFilter extends AuthenticationWebFilter {

  private static final ReactiveAuthenticationManager manager = Mono::just;

  public JwtAuthenticationWebFilter(JwtAuthenticationConverter JwtAuthenticationConverter) {
    super(manager);
    setServerAuthenticationConverter(JwtAuthenticationConverter::apply);
    setRequiresAuthenticationMatcher(
        new AndServerWebExchangeMatcher(
            new NegatedServerWebExchangeMatcher(
                new OrServerWebExchangeMatcher(
                    ServerWebExchangeMatchers.pathMatchers(API_URL_PREFIX + "/newDevice"),
                    ServerWebExchangeMatchers.pathMatchers(API_URL_PREFIX + "/notice"),
                    ServerWebExchangeMatchers.pathMatchers(API_URL_PREFIX + "/versionCheck"),
                    ServerWebExchangeMatchers.pathMatchers(API_URL_PREFIX + "/appTerm"))),
            ServerWebExchangeMatchers.pathMatchers(API_URL_PREFIX + "/**")));
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    return ReactiveSecurityContextHolder.getContext()
        .switchIfEmpty(super.filter(exchange, chain).then(Mono.empty()))
        .flatMap((securityContext) -> chain.filter(exchange));
  }
}
