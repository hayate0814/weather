package com.jp.kinto.app.bff.contractIncludeInfo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.model.contractIncludeInfo.ContractIncludeInfoResponse.CarCharacteristics;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/contractIncludeInfo")
public class ContractIncludeInfoTest extends ApiTest {

  @MockBean private CarMasterGokuApi gokuApi;

  @Test
  @DisplayName("contractIncludeInfo_success")
  void contractIncludeInfo_success() {
    List<CarCharacteristics> carCharacteristicsList =
        parseJsonFile(
            INPUT_ROOT + "/contractIncludeInfo/success_contractIncludeInfo_list_data.json",
            new TypeReference<>() {});
    Mockito.when(gokuApi.getCharacteristics("AGP001", "G001"))
        .thenReturn(Mono.just(carCharacteristicsList));
    webClient
        .get()
        .uri("/api/contractIncludeInfo?makerCode=L01&carModelCode=AGP001&gradeCode=G001")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("contractIncludeInfo_makerCode_error")
  void contractIncludeInfo_makerCode_error() {
    webClient
        .get()
        .uri("/api/contractIncludeInfo?makerCode=L02&carModelCode=AGP001&gradeCode=G001")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("""
            {"code":1,"message":"メーカーコードの値が正しくありません"}
            """);
  }
}
