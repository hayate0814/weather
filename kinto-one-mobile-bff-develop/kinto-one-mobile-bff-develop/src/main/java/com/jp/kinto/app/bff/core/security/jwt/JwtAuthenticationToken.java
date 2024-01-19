package com.jp.kinto.app.bff.core.security.jwt;

import com.jp.kinto.app.bff.model.auth.AuthUser;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Transient;
import org.springframework.security.core.authority.AuthorityUtils;

@Transient
public class JwtAuthenticationToken extends AbstractAuthenticationToken implements Serializable {

  @Serial private static final long serialVersionUID = -956432618194835353L;
  private final Object principal;

  public JwtAuthenticationToken(AuthUser user) {
    super(AuthorityUtils.createAuthorityList("ROLE_" + user.getRole().name()));
    this.principal = user;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return this.principal;
  }

  @Override
  public Object getPrincipal() {
    return this.principal;
  }
}
