package com.jp.kinto.app.bff.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
class GreetController {

  @GetMapping("/greet")
  public Mono<String> hello() {
    return Mono.just("hello world");
  }

  @GetMapping("/healthCheck")
  public Mono<Void> healthCheck(ServerHttpResponse response) {
    response.setStatusCode(HttpStatus.OK);
    return Mono.empty();
  }
}
