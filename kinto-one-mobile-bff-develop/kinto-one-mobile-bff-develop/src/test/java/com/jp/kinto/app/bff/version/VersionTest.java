package com.jp.kinto.app.bff.version;

import com.jp.kinto.app.bff.ApiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("/api/versionCheck")
public class VersionTest extends ApiTest {

  @Test
  @DisplayName("アプリVersionのチェック")
  void versionCheck_success() {
    webClient
        .get()
        .uri("/api/versionCheck")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }
}
