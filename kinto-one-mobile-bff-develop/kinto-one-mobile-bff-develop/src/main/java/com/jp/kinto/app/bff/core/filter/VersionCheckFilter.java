package com.jp.kinto.app.bff.core.filter;

import static com.jp.kinto.app.bff.core.constant.Constant.API_URL_PREFIX;
import static com.jp.kinto.app.bff.core.message.Msg.format;

import com.jp.kinto.app.bff.core.exception.MessageException;
import com.jp.kinto.app.bff.core.exception.VersionCheckException;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.core.properties.AppProperties;
import com.jp.kinto.app.bff.model.AppVersion;
import com.jp.kinto.app.bff.utils.Asserts;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.filter.OrderedWebFilter;
import org.springframework.security.web.server.util.matcher.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class VersionCheckFilter implements OrderedWebFilter {
  @Override
  public int getOrder() {
    return Integer.MAX_VALUE - 1;
  }

  @Autowired private AppProperties appProperties;

  private final ServerWebExchangeMatcher requiresVersionCheckMatcher;

  public VersionCheckFilter() {
    this.requiresVersionCheckMatcher =
        new AndServerWebExchangeMatcher(
            new NegatedServerWebExchangeMatcher(
                new OrServerWebExchangeMatcher(
                    ServerWebExchangeMatchers.pathMatchers(API_URL_PREFIX + "/versionCheck"),
                    ServerWebExchangeMatchers.pathMatchers(API_URL_PREFIX + "/notice"))),
            ServerWebExchangeMatchers.pathMatchers(API_URL_PREFIX + "/**"));
  }

  Function<Optional<String>, AppVersion> getVersion =
      ver -> {
        String version =
            ver.orElseThrow(() -> new MessageException(format(Msg.required.args("Versionヘッダー"))));
        return AppVersion.fromString(version)
            .orElseThrow(() -> new MessageException(format(Msg.invalidValue.args("Versionヘッダー"))));
      };

  Function<AppVersion, Mono<Void>> checkVersion =
      ver -> {
        String latestVersion = null;
        String upgradeRequireLimit = null;
        switch (ver.getDeviceKindSv()) {
          case Android -> {
            latestVersion = appProperties.getLatestVersionAndroid();
            upgradeRequireLimit = appProperties.getUpgradeRequireAndroid();
          }
          case Ios -> {
            latestVersion = appProperties.getLatestVersionIos();
            upgradeRequireLimit = appProperties.getUpgradeRequireIos();
          }
        }

        Asserts.notNull(latestVersion, "設定ファイルにLatestVersionが設定されない");
        Asserts.notNull(upgradeRequireLimit, "設定ファイルに下限Versionが設定されない");
        int result = ver.getVersion().compareTo(upgradeRequireLimit);
        if (result < 0) {
          throw new VersionCheckException(true, upgradeRequireLimit);
        }

        return Mono.empty();
      };

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    return requiresVersionCheckMatcher
        .matches(exchange)
        .filter((matchResult) -> matchResult.isMatch())
        .map(
            (matchResult) ->
                exchange
                    .getRequest()
                    .getHeaders()
                    .getOrDefault("Version", Collections.emptyList())
                    .stream()
                    .findFirst())
        .map(getVersion)
        .flatMap(checkVersion)
        .then(chain.filter(exchange));
  }
}
