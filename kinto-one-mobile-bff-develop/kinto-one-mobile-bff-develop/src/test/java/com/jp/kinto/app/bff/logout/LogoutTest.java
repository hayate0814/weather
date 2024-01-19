package com.jp.kinto.app.bff.logout;

import static org.mockito.ArgumentMatchers.any;

import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.repository.UserDeviceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/logout")
public class LogoutTest extends ApiTest {

  @MockBean private UserDeviceRepository userDeviceRepository;

  @Test
  @DisplayName("正常系")
  void success_01() {
    Mockito.when((userDeviceRepository.updateMemberId(any(), any(), any())))
        .thenReturn(Mono.just(1));

    webClient.post().uri("/api/logout").exchange().expectAll(printResponse).expectStatus().isOk();
  }

  @Test
  @DisplayName("ログアウト処理が失敗")
  void failure_01() {
    Mockito.when((userDeviceRepository.updateMemberId(any(), any(), any())))
        .thenReturn(Mono.just(2));

    webClient
        .post()
        .uri("/api/logout")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .json("{\"code\":500,\"message\":\"[Assertion failed] - ログアウト処理が失敗しました。user_id:1\"}");
  }

  @Test
  @DisplayName("データ取得失敗時")
  void failure_02() {
    Mockito.when((userDeviceRepository.updateMemberId(any(), any(), any())))
        .thenThrow(new RuntimeException("test"));

    webClient
        .post()
        .uri("/api/logout")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError();
  }
}
