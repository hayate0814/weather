package com.jp.kinto.app.bff.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jp.kinto.app.bff.core.exception.BffErrorHandler;
import com.jp.kinto.app.bff.core.properties.AppProperties;
import io.netty.handler.logging.LogLevel;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.codec.CodecProperties;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Slf4j
@Configuration
@EnableWebFlux
public class BffConfig implements WebFluxConfigurer {

  @Autowired private AppProperties appProperties;

  @Autowired private CodecProperties codecProperties;

  @Override
  public void configureHttpMessageCodecs(ServerCodecConfigurer configure) {
    configure.defaultCodecs().maxInMemorySize((int) codecProperties.getMaxInMemorySize().toBytes());
  }

  @Bean
  public Function<String, WebClient> webClient() {
    return baseUrl ->
        WebClient.builder()
            .clientConnector(
                new ReactorClientHttpConnector(
                    HttpClient.create()
                        .wiretap("bff.remote.api", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)))
            .exchangeStrategies(
                ExchangeStrategies.builder()
                    .codecs(
                        configure ->
                            configure
                                .defaultCodecs()
                                .maxInMemorySize(
                                    (int) codecProperties.getMaxInMemorySize().toBytes()))
                    .build())
            .baseUrl(baseUrl)
            .build();
  }

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public ErrorWebExceptionHandler errorWebExceptionHandler(ObjectMapper objectMapper) {
    return new BffErrorHandler(objectMapper);
  }
}
