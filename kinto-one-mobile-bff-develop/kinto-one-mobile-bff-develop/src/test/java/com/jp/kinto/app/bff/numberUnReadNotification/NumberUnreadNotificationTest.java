package com.jp.kinto.app.bff.numberUnReadNotification;

import static org.mockito.ArgumentMatchers.anyInt;

import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.security.jwt.JwtTokenProvider;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import com.jp.kinto.app.bff.model.auth.AuthUser.Role;
import com.jp.kinto.app.bff.repository.MemberNotificationHistoryRepository;
import com.jp.kinto.app.bff.repository.UserDeviceRepository;
import com.jp.kinto.app.bff.repository.entity.UserDevice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/numberUnReadNotification")
public class NumberUnreadNotificationTest extends ApiTest {

  @MockBean private UserDeviceRepository userDeviceRepository;
  @MockBean private MemberNotificationHistoryRepository memberNotificationHistoryRepository;
  @Autowired private JwtTokenProvider jwtTokenProvider;

  @Test
  @DisplayName("未読お知らせ件数取得の正常系")
  void success_01() {
    String mockMemberId = "mem-001";
    Mockito.when(memberNotificationHistoryRepository.getNumberUnreadNotification(mockMemberId))
        .thenReturn(Mono.just(10));

    webClient
        .get()
        .uri("/api/numberUnReadNotification")
        .header(
            "Authorization",
            "Bearer "
                + jwtTokenProvider.createToken(
                    AuthUser.builder().userId(1).memberId(mockMemberId).role(Role.User).build()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("未読お知らせ件数取得=0件の正常系")
  void success_02() {
    Mockito.when(userDeviceRepository.findById(anyInt())).thenReturn(Mono.just(new UserDevice()));
    webClient
        .get()
        .uri("/api/numberUnReadNotification")
        .header(
            "Authorization",
            "Bearer "
                + jwtTokenProvider.createToken(
                    AuthUser.builder().userId(1).role(Role.User).build()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }
}
