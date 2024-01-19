package com.jp.kinto.app.bff.healthCheck;

import com.jp.kinto.app.bff.ApiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("HealthCheck関連処理")
public class HealthCheckTest extends ApiTest {
  @Test
  @DisplayName("healthCheck_success")
  void healthCheck_success() {
    webClient.get().uri("/healthCheck").exchange().expectStatus().isOk();
  }
}
