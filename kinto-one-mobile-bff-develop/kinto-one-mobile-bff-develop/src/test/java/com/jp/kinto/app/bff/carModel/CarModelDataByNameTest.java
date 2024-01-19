package com.jp.kinto.app.bff.carModel;

import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.response.CarModelDetailRes.Grade;
import com.jp.kinto.app.bff.goku.response.CarModelDetailRes.SelectableContract;
import com.jp.kinto.app.bff.goku.response.InsertMessageDataRes;
import com.jp.kinto.app.bff.goku.response.MonthlyPriceRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/carModel/name/{name}")
public class CarModelDataByNameTest extends ApiTest {

  @MockBean private CarMasterGokuApi carMasterGokuApi;

  @Test
  @DisplayName("正常系")
  void success_01() {
    Mockito.when(carMasterGokuApi.getCarModelId(any())).thenReturn(Mono.just("test"));

    SelectableContract selectableContract =
        parseJsonFile(
            INPUT_ROOT + "/carModel/success_selectable_contract_data.json",
            new TypeReference<>() {});
    Mockito.when(carMasterGokuApi.getSelectableContract(any()))
        .thenReturn(Mono.just(selectableContract));

    Grade[] grades =
        parseJsonFile(
            INPUT_ROOT + "/carModel/success_grade_list_data.json", new TypeReference<>() {});
    Mockito.when(carMasterGokuApi.getGrades(any(), any())).thenReturn(Mono.just(grades));

    MonthlyPriceRes monthlyPriceRes =
        parseJsonFile(
            INPUT_ROOT + "/carModel/success_monthly_fee_data.json", new TypeReference<>() {});
    Mockito.when(carMasterGokuApi.getMonthlyPrice(any())).thenReturn(Mono.just(monthlyPriceRes));

    InsertMessageDataRes[] insertMessageDataRes =
        parseJsonFile(
            INPUT_ROOT + "/carModel/success_grade_list_data.json", new TypeReference<>() {});
    Mockito.when(carMasterGokuApi.getInsertMessageData())
        .thenReturn(Mono.just(insertMessageDataRes));

    webClient
        .get()
        .uri("/api/carModel/name/1?planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("データが取得できません")
  void failure_01() {
    Mockito.when(carMasterGokuApi.getCarModelId(any()))
        .thenReturn(
            Mono.error(mockGokuApiMessageExceptionWhenMasterApiCall(HttpStatus.NOT_FOUND, "xxx")));
    webClient
        .get()
        .uri("/api/carModel/name/xxx?planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json(
            """
            {"code":2,"message":"KINTO ONEをご検討いただきありがとうございます。
            申し訳ございませんが、お取り扱い期間や予定数の終了により、ご選択いただいた車はお申し込みいただけません。"}
            """);
  }

  @Test
  @DisplayName("正常系")
  void failure_02() {
    Mockito.when(carMasterGokuApi.getCarModelId(any())).thenReturn(Mono.just(""));
    webClient
        .get()
        .uri("/api/carModel/name/1?planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("{\"code\":1,\"message\":\"車種IDは必須項目です\"}");
  }
}
