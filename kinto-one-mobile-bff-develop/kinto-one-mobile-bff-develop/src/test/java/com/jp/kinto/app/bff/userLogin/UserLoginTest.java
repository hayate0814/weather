package com.jp.kinto.app.bff.userLogin;

import static com.jp.kinto.app.bff.core.constant.Constant.JPN_KINTO_ID_AUTH;
import static org.mockito.ArgumentMatchers.any;

import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.goku.JpIdApi;
import com.jp.kinto.app.bff.goku.response.JpIdAuthorizationInfoRes;
import com.jp.kinto.app.bff.goku.response.JpIdLoginRes;
import com.jp.kinto.app.bff.repository.LoginHistoryRepository;
import com.jp.kinto.app.bff.repository.UserDeviceRepository;
import com.jp.kinto.app.bff.repository.entity.LoginHistory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.util.MultiValueMapAdapter;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/login")
public class UserLoginTest extends ApiTest {

  @MockBean private JpIdApi jpIdApi;
  @MockBean private UserDeviceRepository userDeviceRepository;

  @MockBean private LoginHistoryRepository loginHistoryRepository;

  @BeforeEach
  public void setUp() {
    setupCommonMocks();
  }

  private void setupCommonMocks() {
    Map<String, List<ResponseCookie>> cookie = new HashMap<>();
    cookie.put(
        JPN_KINTO_ID_AUTH,
        List.of(ResponseCookie.from("name", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9").build()));

    JpIdLoginRes response = new JpIdLoginRes();
    response.setRedirectUrl("xxx");
    response.setCookies(new MultiValueMapAdapter<>(cookie));
    Mockito.when(jpIdApi.login(any(), any())).thenReturn(Mono.just(response));

    var jpIdAuthorizationInfoRes = new JpIdAuthorizationInfoRes();
    jpIdAuthorizationInfoRes.setId("MEM-0420189550");
    jpIdAuthorizationInfoRes.setType("PERSONAL");
    jpIdAuthorizationInfoRes.setEmail("abc@gmail.com");
    jpIdAuthorizationInfoRes.setMailActive(true);
    jpIdAuthorizationInfoRes.setMemberShip(false);
    Mockito.when(jpIdApi.authorizationInfo(any())).thenReturn(Mono.just(jpIdAuthorizationInfoRes));

    LoginHistory existingLoginHistory = new LoginHistory();
    Mockito.when(loginHistoryRepository.findOneByUserAndMemberId(any(), any()))
        .thenReturn(Mono.just(existingLoginHistory));

    Mockito.when(loginHistoryRepository.save(any(LoginHistory.class)))
        .thenReturn(Mono.just(existingLoginHistory));
  }

  @Test
  @DisplayName("正常系")
  void success_01() {
    Mockito.when(userDeviceRepository.updateMemberId(any(), any(), any())).thenReturn(Mono.just(1));

    webClient
        .post()
        .uri("/api/login")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            """
                    {
                         "email":"abc@gmail.com",
                         "password": "123456"
                     }
                    """)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("メールアドレスとパスワード必須")
  void fail_02() {
    webClient
        .post()
        .uri("/api/login")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            """
                    {
                         "email":"",
                         "password": "123456"
                     }
                    """)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json(
            """
                    {
                      "code": 1,
                      "message": "%s"
                    }
                    """
                .formatted(Msg.required.args("メールアドレスとパスワード")));
  }

  @Test
  @DisplayName("無効なメールアドレス、パスワード")
  void fail_03() {
    Mockito.when(userDeviceRepository.updateMemberId(any(), any(), any())).thenReturn(Mono.just(2));
    webClient
        .post()
        .uri("/api/login")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(
            """
                    {
                         "email":"abc@gmail.com",
                         "password": "123456"
                     }
                    """)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .json(
            "{\"code\":500,\"message\":\"[Assertion failed] - メンバーIDの更新処理が失敗しました。user_id:1,"
                + " member_id:MEM-0420189550\"}");
  }
}
