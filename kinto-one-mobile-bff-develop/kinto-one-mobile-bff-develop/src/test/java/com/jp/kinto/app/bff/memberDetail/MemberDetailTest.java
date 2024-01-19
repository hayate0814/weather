package com.jp.kinto.app.bff.memberDetail;

import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.security.jwt.JwtTokenProvider;
import com.jp.kinto.app.bff.goku.JpIdApi;
import com.jp.kinto.app.bff.goku.response.JpIdMemberInfoRes;
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
@DisplayName("/api/memberDetail")
public class MemberDetailTest extends ApiTest {

  @MockBean private JpIdApi jpIdApi;
  @MockBean private UserDeviceRepository userDeviceRepository;
  @MockBean private MemberNotificationHistoryRepository memberNotificationHistoryRepository;
  @Autowired private JwtTokenProvider jwtTokenProvider;

  @Test
  @DisplayName("AuthUserのメンバIDが存在の正常系")
  void success_01() {
    JpIdMemberInfoRes jpIdMemberInfoRes =
        parseJsonFile(
            INPUT_ROOT + "/memberDetail/success_member_detail_data.json", new TypeReference<>() {});
    Mockito.when(jpIdApi.personal(any(String.class), any(String.class)))
        .thenReturn(Mono.just(jpIdMemberInfoRes));

    webClient
        .get()
        .uri("/api/memberDetail")
        .header(
            "Authorization",
            "Bearer "
                + jwtTokenProvider.createToken(
                    AuthUser.builder()
                        .userId(1)
                        .jpnKintoIdAuth("a")
                        .memberId("aaa")
                        .role(Role.User)
                        .build()))
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
    JpIdMemberInfoRes jpIdMemberInfoRes =
        parseJsonFile(
            INPUT_ROOT + "/memberDetail/success_member_detail_data.json", new TypeReference<>() {});
    Mockito.when(jpIdApi.personal(any(String.class), any(String.class)))
        .thenReturn(Mono.just(jpIdMemberInfoRes));

    webClient
        .get()
        .uri("/api/memberDetail")
        .header(
            "Authorization",
            "Bearer "
                + jwtTokenProvider.createToken(
                    AuthUser.builder().userId(1).jpnKintoIdAuth("a").role(Role.User).build()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("メンバIDが存在しない場合")
  void success_03() {
    UserDevice userDevice = new UserDevice();
    Mockito.when(userDeviceRepository.findById(1)).thenReturn(Mono.just(userDevice));

    webClient
        .get()
        .uri("/api/memberDetail")
        .header(
            "Authorization",
            "Bearer "
                + jwtTokenProvider.createToken(
                    AuthUser.builder().userId(1).jpnKintoIdAuth("a").role(Role.User).build()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  @DisplayName("ログイン時に発行されるcookieが存在しない場合")
  void success_04() {
    UserDevice userDevice = new UserDevice();
    Mockito.when(userDeviceRepository.findById(1)).thenReturn(Mono.just(userDevice));

    webClient
        .get()
        .uri("/api/memberDetail")
        .header(
            "Authorization",
            "Bearer "
                + jwtTokenProvider.createToken(
                    AuthUser.builder().userId(1).memberId("a").role(Role.User).build()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  @DisplayName("データ取得失敗")
  void fail_01() {
    Mockito.when(userDeviceRepository.findById(1)).thenThrow(new RuntimeException("test"));

    webClient
        .get()
        .uri("/api/memberDetail")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError();
  }

  @Test
  @DisplayName("JPIDデータ取得失敗")
  void fail_02() {
    Mockito.when(jpIdApi.personal(any(String.class), any(String.class)))
        .thenThrow(new RuntimeException("test"));

    webClient
        .get()
        .uri("/api/memberDetail")
        .header(
            "Authorization",
            "Bearer "
                + jwtTokenProvider.createToken(
                    AuthUser.builder()
                        .userId(1)
                        .memberId("a")
                        .jpnKintoIdAuth("a")
                        .role(Role.User)
                        .build()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError();
  }
}
