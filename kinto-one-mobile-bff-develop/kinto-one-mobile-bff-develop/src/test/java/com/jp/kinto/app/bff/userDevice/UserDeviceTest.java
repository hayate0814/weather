package com.jp.kinto.app.bff.userDevice;

import static com.jp.kinto.app.bff.core.constant.Constant.INTERNAL_ERROR_CODE;
import static org.mockito.ArgumentMatchers.any;

import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.repository.UserDeviceRepository;
import com.jp.kinto.app.bff.repository.entity.UserDevice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/newDevice")
public class UserDeviceTest extends ApiTest {
  @MockBean private UserDeviceRepository userDeviceRepository;

  @Test
  @DisplayName("Android新規")
  void android_success() {
    var returnUserDevice = new UserDevice();
    returnUserDevice.setUserId(1);

    Mockito.when((userDeviceRepository.save(any(UserDevice.class))))
        .thenReturn(Mono.just(returnUserDevice));
    webClient
        .post()
        .uri("/api/newDevice?kindSv=1")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("IOS新規")
  void ios_success() {
    var returnUserDevice = new UserDevice();
    returnUserDevice.setUserId(2);
    Mockito.when((userDeviceRepository.save(any(UserDevice.class))))
        .thenReturn(Mono.just(returnUserDevice));

    webClient
        .post()
        .uri("/api/newDevice?kindSv=2")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.userId")
        .isEqualTo(2);
  }

  @Test
  @DisplayName("無効なkindSv")
  void fail_01() {
    var returnUserDevice = new UserDevice();
    returnUserDevice.setUserId(2);
    Mockito.when((userDeviceRepository.save(any(UserDevice.class))))
        .thenReturn(Mono.just(returnUserDevice));

    webClient
        .post()
        .uri("/api/newDevice?kindSv=3")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json(
            """
                    {
                      "code": 1,
                      "message": "端末種別の値が正しくありません"
                    }
                    """);
  }

  @Test
  @DisplayName("kindSv必須")
  void fail_02() {
    var returnUserDevice = new UserDevice();
    returnUserDevice.setUserId(2);
    Mockito.when((userDeviceRepository.save(any(UserDevice.class))))
        .thenReturn(Mono.just(returnUserDevice));

    webClient
        .post()
        .uri("/api/newDevice")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo(1);
  }

  @Test
  @DisplayName("DB保存失敗")
  void fail_03() {
    var returnUserDevice = new UserDevice();
    returnUserDevice.setUserId(2);
    Mockito.when((userDeviceRepository.save(any(UserDevice.class))))
        .thenThrow(new RuntimeException("test db exception."));

    webClient
        .post()
        .uri("/api/newDevice?kindSv=1")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .jsonPath("$.code")
        .isEqualTo(INTERNAL_ERROR_CODE);
  }
}
