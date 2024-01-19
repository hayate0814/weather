package com.jp.kinto.app.bff.core.security;

import static com.jp.kinto.app.bff.core.constant.Constant.API_URL_PREFIX;
import static com.jp.kinto.app.bff.model.auth.AuthUser.Role.Member;
import static com.jp.kinto.app.bff.model.auth.AuthUser.Role.User;

import com.jp.kinto.app.bff.core.properties.JwtProperties;
import com.jp.kinto.app.bff.core.security.jwt.JwtAuthenticationWebFilter;
import com.jp.kinto.app.bff.core.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {

  @Autowired private ApplicationContext appContext;
  @Autowired private JwtProperties jwtProperties;

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    WebHookAccessChecker webHookAccessChecker = appContext.getBean(WebHookAccessChecker.class);

    return http.authorizeExchange(
            exchanges ->
                exchanges
                    .pathMatchers("/healthCheck")
                    .permitAll()
                    .pathMatchers(API_URL_PREFIX + "/newDevice")
                    .permitAll()
                    .pathMatchers(API_URL_PREFIX + "/versionCheck")
                    .permitAll()
                    .pathMatchers(API_URL_PREFIX + "/appTerm")
                    .permitAll()
                    .pathMatchers(API_URL_PREFIX + "/notice")
                    .access(webHookAccessChecker)
                    .pathMatchers(API_URL_PREFIX + "/**")
                    .hasAnyRole(User.name(), Member.name())
                    .anyExchange()
                    .authenticated())
        .addFilterAt(
            appContext.getBean(JwtAuthenticationWebFilter.class),
            SecurityWebFiltersOrder.AUTHENTICATION)
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
        .build();
  }

  @Bean
  public JwtTokenProvider jwtTokenProvider() {
    return new JwtTokenProvider(jwtProperties.getSecretKey());
  }
}
