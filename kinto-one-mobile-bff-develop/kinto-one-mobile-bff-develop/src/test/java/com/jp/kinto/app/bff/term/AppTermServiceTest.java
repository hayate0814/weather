package com.jp.kinto.app.bff.term;

import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.properties.AppProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

@SpringBootTest
@DisplayName("/api/appTerm")
public class AppTermServiceTest extends ApiTest {
  @Autowired private AppProperties appProperties;

  @Test
  @DisplayName("アプリ起動時利用規約取得")
  void getAppTerm() throws IOException {
    String expectedContent =
        Files.readString(Paths.get(new ClassPathResource("html/appTerm.html").getURI()));

    webClient
        .get()
        .uri("/api/appTerm")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.termVersion")
        .isEqualTo(appProperties.getLatestTermVersion())
        .jsonPath("$.content")
        .isNotEmpty();
  }
}
