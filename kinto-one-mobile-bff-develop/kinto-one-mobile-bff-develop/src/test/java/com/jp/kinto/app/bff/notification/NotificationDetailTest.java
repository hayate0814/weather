package com.jp.kinto.app.bff.notification;

import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.security.jwt.JwtTokenProvider;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import com.jp.kinto.app.bff.model.auth.AuthUser.Role;
import com.jp.kinto.app.bff.repository.MemberNotificationHistoryRepository;
import com.jp.kinto.app.bff.repository.UserDeviceRepository;
import com.jp.kinto.app.bff.repository.entity.NotificationEntity;
import com.jp.kinto.app.bff.repository.entity.UserDevice;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/notificationDetail")
public class NotificationDetailTest extends ApiTest {

  @MockBean private UserDeviceRepository userDeviceRepository;
  @MockBean private MemberNotificationHistoryRepository memberNotificationHistoryRepository;
  @Autowired private JwtTokenProvider jwtTokenProvider;

  @Test
  @DisplayName("AuthUserのメンバIDが存在の正常系")
  void success_01() {
    NotificationEntity notificationEntity = new NotificationEntity();
    notificationEntity.setNotificationId(11);
    notificationEntity.setCreateDatetime(LocalDateTime.now());
    Mockito.when(memberNotificationHistoryRepository.findByIdAndMemberId(1, "aaa"))
        .thenReturn(Mono.just(notificationEntity));
    Mockito.when(memberNotificationHistoryRepository.save(notificationEntity))
        .thenReturn(Mono.just(notificationEntity));

    webClient
        .get()
        .uri("/api/notificationDetail/1")
        .header(
            "Authorization",
            "Bearer "
                + jwtTokenProvider.createToken(
                    AuthUser.builder().userId(1).memberId("aaa").role(Role.User).build()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("AuthUserのメンバIDが存在しないの正常系")
  void success_02() {
    UserDevice userDevice = new UserDevice();
    userDevice.setMemberId("1");
    Mockito.when(userDeviceRepository.findById(1)).thenReturn(Mono.just(userDevice));

    NotificationEntity notificationEntity = new NotificationEntity();
    notificationEntity.setNotificationId(11);
    notificationEntity.setCreateDatetime(LocalDateTime.now());
    Mockito.when(memberNotificationHistoryRepository.findByIdAndMemberId(1, "1"))
        .thenReturn(Mono.just(notificationEntity));
    Mockito.when(memberNotificationHistoryRepository.save(notificationEntity))
        .thenReturn(Mono.just(notificationEntity));

    webClient
        .get()
        .uri("/api/notificationDetail/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("メンバIDが存在しない場合")
  void fail_01() {
    UserDevice userDevice = new UserDevice();
    Mockito.when(userDeviceRepository.findById(1)).thenReturn(Mono.just(userDevice));

    webClient
        .get()
        .uri("/api/notificationDetail/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("{\"code\":2,\"message\":\"ログインしてから再度検索をお願い致します\"}");
  }

  @Test
  @DisplayName("対象通知IDが存在しない場合")
  void fail_02() {
    UserDevice userDevice = new UserDevice();
    userDevice.setMemberId("1");
    Mockito.when(userDeviceRepository.findById(1)).thenReturn(Mono.just(userDevice));
    Mockito.when(memberNotificationHistoryRepository.findByIdAndMemberId(1, "1"))
        .thenReturn(Mono.empty());

    webClient
        .get()
        .uri("/api/notificationDetail/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("{\"code\":2,\"message\":\"該当通知情報は存在しません。再度検索をお願い致します\"}");
  }

  @Test
  @DisplayName("データ取得失敗")
  void fail_03() {
    Mockito.when(userDeviceRepository.findById(1)).thenThrow(new RuntimeException("test"));

    webClient
        .get()
        .uri("/api/notificationDetail/1")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError();
  }
}
