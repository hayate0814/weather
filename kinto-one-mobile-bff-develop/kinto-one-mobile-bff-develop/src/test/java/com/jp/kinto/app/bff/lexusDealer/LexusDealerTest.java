package com.jp.kinto.app.bff.lexusDealer;

import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.response.LexusDealerRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/lexusDealerList")
public class LexusDealerTest extends ApiTest {

  @MockBean private CarMasterGokuApi gokuApi;

  @Test
  @DisplayName("都道府県コード")
  void success_01() {
    LexusDealerRes lexusDealerRes =
        parseJsonFile(
            INPUT_ROOT + "/lexusDealer/success_lexus_dealer_data1.json", new TypeReference<>() {});
    Mockito.when(gokuApi.getLexusDealerByPrefectureCode(any()))
        .thenReturn(Mono.just(lexusDealerRes));
    webClient
        .get()
        .uri("/api/lexusDealerList?prefectureCode=84")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("店舗名")
  void success_02() {
    LexusDealerRes lexusDealerRes =
        parseJsonFile(
            INPUT_ROOT + "/lexusDealer/success_lexus_dealer_data2.json", new TypeReference<>() {});
    Mockito.when(gokuApi.getLexusDealerByStoreName(any())).thenReturn(Mono.just(lexusDealerRes));
    webClient
        .get()
        .uri("/api/lexusDealerList?storeName=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("パラメータ必須")
  void fail_01() {
    webClient
        .get()
        .uri("/api/lexusDealerList")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("{\"code\":1,\"message\":\"リクエストのパラメータは正しくありません\"}");
  }

  @Test
  @DisplayName("パラメータは正しくありません")
  void fail_02() {
    webClient
        .get()
        .uri("/api/lexusDealerList?prefectureCode=84&storeName=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("{\"code\":1,\"message\":\"リクエストのパラメータは正しくありません\"}");
  }

  @Test
  @DisplayName("都道府県にデータ取得失敗")
  void fail_03() {
    Mockito.when(gokuApi.getLexusDealerByPrefectureCode(any()))
        .thenThrow(new RuntimeException("test"));
    webClient
        .get()
        .uri("/api/lexusDealerList?prefectureCode=84")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError();
  }

  @Test
  @DisplayName("店舗名にデータ取得失敗")
  void fail_04() {
    Mockito.when(gokuApi.getLexusDealerByStoreName(any())).thenThrow(new RuntimeException("test"));
    webClient
        .get()
        .uri("/api/lexusDealerList?storeName=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError();
  }
}
