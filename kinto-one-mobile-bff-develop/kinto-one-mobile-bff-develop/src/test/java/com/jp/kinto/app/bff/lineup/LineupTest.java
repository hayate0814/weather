package com.jp.kinto.app.bff.lineup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.response.CarDataRes;
import com.jp.kinto.app.bff.goku.response.DeliveryDateRes;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/lineup")
public class LineupTest extends ApiTest {

  @MockBean private CarMasterGokuApi carMasterGokuApi;

  @Test
  @DisplayName("トヨタ正常系")
  void success_toyota() {
    List<CarDataRes> carDataList =
        parseJsonFile(INPUT_ROOT + "/lineup/success_toyota_cardata.json", new TypeReference<>() {});
    DeliveryDateRes deliveryDateRes =
        parseJsonFile(INPUT_ROOT + "/lineup/success_toyota_delivery.json", DeliveryDateRes.class);

    Mockito.when((carMasterGokuApi.getCarDataJson())).thenReturn(Mono.just(carDataList));

    Mockito.when((carMasterGokuApi.getDeliveryData())).thenReturn(Mono.just(deliveryDateRes));

    webClient
        .get()
        .uri("/api/lineup?makerCode=T01")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("Lexus正常系")
  void success_lexus() {
    List<CarDataRes> carDataList =
        parseJsonFile(INPUT_ROOT + "/lineup/success_toyota_cardata.json", new TypeReference<>() {});
    DeliveryDateRes deliveryDateRes =
        parseJsonFile(INPUT_ROOT + "/lineup/success_toyota_delivery.json", DeliveryDateRes.class);

    Mockito.when((carMasterGokuApi.getCarDataJson())).thenReturn(Mono.just(carDataList));

    Mockito.when((carMasterGokuApi.getDeliveryData())).thenReturn(Mono.just(deliveryDateRes));

    webClient
        .get()
        .uri("/api/lineup?makerCode=L01")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("メーカーコード必須")
  void fail_01() {
    webClient
        .get()
        .uri("/api/lineup?makerCode=")
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
                .formatted(Msg.required.args("メーカーコード")));
  }

  @Test
  @DisplayName("無効なメーカーコード")
  void fail_02() {
    webClient
        .get()
        .uri("/api/lineup?makerCode=X01")
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
                .formatted(Msg.invalidValue.args("メーカーコード")));
  }

  @Test
  @DisplayName("deliverydate.jsonデータ取得失敗時")
  void fail_03() {
    List<CarDataRes> carDataList =
        parseJsonFile(INPUT_ROOT + "/lineup/success_toyota_cardata.json", new TypeReference<>() {});
    DeliveryDateRes deliveryDateRes = new DeliveryDateRes();

    Mockito.when((carMasterGokuApi.getCarDataJson())).thenReturn(Mono.just(carDataList));

    Mockito.when((carMasterGokuApi.getDeliveryData())).thenReturn(Mono.just(deliveryDateRes));

    webClient
        .get()
        .uri("/api/lineup?makerCode=L01")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError();
  }

  @Test
  @DisplayName("cardata.jsonデータ取得失敗時")
  void fail_04() {
    List<CarDataRes> carDataList =
        parseJsonFile(INPUT_ROOT + "/lineup/success_toyota_cardata.json", new TypeReference<>() {});

    Mockito.when((carMasterGokuApi.getCarDataJson())).thenReturn(Mono.just(carDataList));

    Mockito.when((carMasterGokuApi.getDeliveryData())).thenThrow(new RuntimeException("test"));

    webClient
        .get()
        .uri("/api/lineup?makerCode=L01")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError();
  }
}
