package com.jp.kinto.app.bff.membersRegisterCheck;

import static org.mockito.ArgumentMatchers.any;

import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.goku.JpIdApi;
import com.jp.kinto.app.bff.goku.response.JpidMembersRegisterCheckRes;
import com.jp.kinto.app.bff.model.registerCheck.MembersRegisterCheckRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/members/register-check")
public class MembersRegisterCheckTest extends ApiTest {

  @MockBean private JpIdApi jpIdApi;

  @Test
  @DisplayName("正常系 - 既存にあるメールアドレス")
  void success_01() {
    var mockResponse = new JpidMembersRegisterCheckRes();
    mockResponse.setRegister(false);

    Mockito.when(jpIdApi.membersRegisterCheck(any())).thenReturn(Mono.just(mockResponse));

    var request = new MembersRegisterCheckRequest();
    request.setEmail("xxxx@gmail.com");

    webClient
        .post()
        .uri("/api/members/register-check")
        .body(Mono.just(request), MembersRegisterCheckRequest.class)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk()
        .expectBody()
        .json("""
          {
              "register": false
          }
          """);
  }

  @Test
  @DisplayName("正常系 - 既存にないメールアドレス")
  void success_02() {
    var mockResponse = new JpidMembersRegisterCheckRes();
    mockResponse.setRegister(true);

    Mockito.when(jpIdApi.membersRegisterCheck(any())).thenReturn(Mono.just(mockResponse));

    var request = new MembersRegisterCheckRequest();
    request.setEmail("xxxx@gmail.com");

    webClient
        .post()
        .uri("/api/members/register-check")
        .body(Mono.just(request), MembersRegisterCheckRequest.class)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk()
        .expectBody()
        .json("""
            {
                "register": true
            }
            """);
  }

  @Test
  @DisplayName("異常系 - メールアドレス未設定")
  void fail_01() {
    var request = new MembersRegisterCheckRequest();

    webClient
        .post()
        .uri("/api/members/register-check")
        .body(Mono.just(request), MembersRegisterCheckRequest.class)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is4xxClientError()
        .expectBody()
        .json(
            """
            {
                "code": 1,
                "message": "%s"
            }
            """
                .formatted(Msg.required.args("メールアドレス")));
  }
}
