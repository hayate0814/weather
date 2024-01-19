package com.jp.kinto.app.bff.core.security.jwt;

import com.jp.kinto.app.bff.model.auth.AuthUser;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

public class JwtReactiveAuthorizationManager
    implements ReactiveAuthorizationManager<AuthorizationContext> {
  @Override
  public Mono<AuthorizationDecision> check(
      Mono<Authentication> authentication, AuthorizationContext context) {
    return authentication.map(
        x ->
            new AuthorizationDecision(
                x.isAuthenticated() && x.getCredentials() instanceof AuthUser));
  }
}
