package com.jp.kinto.app.bff.dealer;

import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.response.DealerRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/dealerList")
public class DealerTest extends ApiTest {
  @MockBean private CarMasterGokuApi carMasterGokuApi;

  @Test
  @DisplayName("dealerSearch_success")
  void dealerSearch_success() {
    DealerRes dealerRes =
        parseJsonFile(INPUT_ROOT + "/dealer/success_dealer.json", new TypeReference<>() {});

    Mockito.when(carMasterGokuApi.getDealerByRange(any(), any(), any()))
        .thenReturn(Mono.just(dealerRes));
    webClient
        .get()
        .uri("/api/dealerList?carModelEnglishName=roomy&latitude=35.70371&longitude=139.748843")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("dealerSearch_noCarName")
  void dealerSearch_noCarName() {

    webClient
        .get()
        .uri("/api/dealerList?latitude=35.70371&longitude=139.748843")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("{\"code\":1,\"message\":\"車種英語名は必須項目です\"}");
  }

  @Test
  @DisplayName("dealerSearch_noLat")
  void dealerSearch_noLat() {

    webClient
        .get()
        .uri("/api/dealerList?carModelEnglishName=roomy&longitude=139.748843")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("{\"code\":1,\"message\":\"緯度は必須項目です\"}");
  }

  @Test
  @DisplayName("dealerSearch_noLon")
  void dealerSearch_noLon() {

    webClient
        .get()
        .uri("/api/dealerList?carModelEnglishName=roomy&latitude=35.70371")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("{\"code\":1,\"message\":\"経度は必須項目です\"}");
  }
}
