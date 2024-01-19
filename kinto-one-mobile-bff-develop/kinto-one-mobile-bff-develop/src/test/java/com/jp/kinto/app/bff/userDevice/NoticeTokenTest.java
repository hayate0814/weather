package com.jp.kinto.app.bff.userDevice;

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
@DisplayName("/api/noticeToken")
public class NoticeTokenTest extends ApiTest {
  @MockBean private UserDeviceRepository userDeviceRepository;

  @Test
  @DisplayName("通知トークン更新")
  void update_success() {
    Mockito.when(userDeviceRepository.updateNoticeToken(any(), any(), any()))
        .thenReturn(Mono.just(1));

    webClient
        .post()
        .uri("/api/noticeToken?token=tokenTest")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("通知トークン更新error")
  void update_error() {

    webClient
        .post()
        .uri("/api/noticeToken")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest();
  }
}
