package com.jp.kinto.app.bff.userInfo;

import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.core.security.jwt.JwtAuthenticationConverter;
import com.jp.kinto.app.bff.core.security.jwt.JwtAuthenticationToken;
import com.jp.kinto.app.bff.goku.JpIdApi;
import com.jp.kinto.app.bff.goku.response.JpIdMemberInfoRes;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/user")
public class UserInfoTest extends ApiTest {

  @MockBean private JpIdApi jpIdApi;
  @MockBean private JwtAuthenticationConverter jwtAuthenticationConverter;

  @Test
  @DisplayName("ゲストユーザー取得")
  void getGuestUser() {
    JwtAuthenticationToken auth =
        new JwtAuthenticationToken(AuthUser.builder().userId(1).role(AuthUser.Role.User).build());
    Mockito.when(jwtAuthenticationConverter.apply(any(ServerWebExchange.class)))
        .thenReturn(Mono.just(auth));
    webClient
        .get()
        .uri("/api/user")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk()
        .expectBody()
        .json(
            """
                    {"userId":1,"memberId":null,"memberType":null,"memberName":"Guest"}
                    """);
  }

  @Test
  @DisplayName("メンバーユーザー取得")
  void getMemberUser() {
    JwtAuthenticationToken auth =
        new JwtAuthenticationToken(
            AuthUser.builder()
                .userId(1)
                .memberId("MEM-0120001147")
                .memberType(Constant.MemberType.fromString("PERSONAL"))
                .jpnKintoIdAuth("testCookie")
                .role(AuthUser.Role.Member)
                .build());
    Mockito.when(jwtAuthenticationConverter.apply(any(ServerWebExchange.class)))
        .thenReturn(Mono.just(auth));

    JpIdMemberInfoRes jpIdMemberInfoRes =
        parseJsonFile(
            INPUT_ROOT + "/memberDetail/success_member_detail_data.json", new TypeReference<>() {});
    Mockito.when(jpIdApi.personal(any(String.class), any(String.class)))
        .thenReturn(Mono.just(jpIdMemberInfoRes));

    webClient
        .get()
        .uri("/api/user")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk()
        .expectBody()
        .json(
            """
                    {"userId":1,"memberId":"MEM-0120001147","memberType":"Personal","memberName":"テスト 個人いち"}
                    """);
  }
}
