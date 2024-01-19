package com.jp.kinto.app.bff.carModel;

import static com.jp.kinto.app.bff.core.exception.GokuApiMessageException.GOKU_ERROR_CODE_ENTITY_NOT_FOUND;
import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.message.Msg;
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
@DisplayName("/api/carModel/carModelId/{carModelId}")
public class CarModelDataByIdTest extends ApiTest {

  @MockBean private CarMasterGokuApi carMasterGokuApi;

  @Test
  @DisplayName("正常系")
  void success_01() {
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
        .uri("/api/carModel/carModelId/1?planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("データが取得できません")
  void failure_01() {
    Mockito.when(carMasterGokuApi.getSelectableContract(any()))
        .thenReturn(
            Mono.error(
                mockGokuApiMessageExceptionWhenMasterApiCall(
                    HttpStatus.NOT_FOUND, GOKU_ERROR_CODE_ENTITY_NOT_FOUND)));

    webClient
        .get()
        .uri("/api/carModel/carModelId/1?planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is4xxClientError()
        .expectBody()
        .json(
            """
                {
                  "code": 3,
                  "message": "%s"
                }
                """
                .formatted(Msg.VehicleDataExpired));
  }

  @Test
  @DisplayName("グレートデータが取得できません")
  void failure_02() {
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
    monthlyPriceRes.setGrades(null);
    Mockito.when(carMasterGokuApi.getMonthlyPrice(any())).thenReturn(Mono.just(monthlyPriceRes));

    InsertMessageDataRes[] insertMessageDataRes =
        parseJsonFile(
            INPUT_ROOT + "/carModel/success_grade_list_data.json", new TypeReference<>() {});
    Mockito.when(carMasterGokuApi.getInsertMessageData())
        .thenReturn(Mono.just(insertMessageDataRes));

    webClient
        .get()
        .uri("/api/carModel/carModelId/1?planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .json(
            "{\"code\":500,\"message\":\"[Assertion failed] -"
                + " [車種(id:CAR-0000000356)の選択グレートデータが取得できません] must not be empty: it must contain at"
                + " least 1 element\"}");
  }

  @Test
  @DisplayName("外板色データが取得できません")
  void failure_03() {
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
    monthlyPriceRes.getGrades()[0].setOuterColors(null);
    Mockito.when(carMasterGokuApi.getMonthlyPrice(any())).thenReturn(Mono.just(monthlyPriceRes));

    InsertMessageDataRes[] insertMessageDataRes =
        parseJsonFile(
            INPUT_ROOT + "/carModel/success_grade_list_data.json", new TypeReference<>() {});
    Mockito.when(carMasterGokuApi.getInsertMessageData())
        .thenReturn(Mono.just(insertMessageDataRes));

    webClient
        .get()
        .uri("/api/carModel/carModelId/1?planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .json(
            "{\"code\":500,\"message\":\"[Assertion failed] -"
                + " [グレート(id:GRD-0000001126)の選択外板色データが取得できません] must not be empty: it must contain"
                + " at least 1 element\"}");
  }

  @Test
  @DisplayName("内装色データが取得できません")
  void failure_04() {
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
    monthlyPriceRes.getGrades()[0].setInteriorColors(null);
    Mockito.when(carMasterGokuApi.getMonthlyPrice(any())).thenReturn(Mono.just(monthlyPriceRes));

    InsertMessageDataRes[] insertMessageDataRes =
        parseJsonFile(
            INPUT_ROOT + "/carModel/success_grade_list_data.json", new TypeReference<>() {});
    Mockito.when(carMasterGokuApi.getInsertMessageData())
        .thenReturn(Mono.just(insertMessageDataRes));

    webClient
        .get()
        .uri("/api/carModel/carModelId/1?planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .json(
            "{\"code\":500,\"message\":\"[Assertion failed] -"
                + " [グレート(id:GRD-0000001126)の選択内装色データが取得できません] must not be empty: it must contain"
                + " at least 1 element\"}");
  }

  @Test
  @DisplayName("パッケージデータが取得できません")
  void failure_05() {
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
    monthlyPriceRes.getGrades()[0].setPackages(null);
    Mockito.when(carMasterGokuApi.getMonthlyPrice(any())).thenReturn(Mono.just(monthlyPriceRes));

    InsertMessageDataRes[] insertMessageDataRes =
        parseJsonFile(
            INPUT_ROOT + "/carModel/success_grade_list_data.json", new TypeReference<>() {});
    Mockito.when(carMasterGokuApi.getInsertMessageData())
        .thenReturn(Mono.just(insertMessageDataRes));

    webClient
        .get()
        .uri("/api/carModel/carModelId/1?planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .json(
            "{\"code\":500,\"message\":\"[Assertion failed] -"
                + " [グレート(id:GRD-0000001126)の選択パッケージデータが取得できません] must not be empty: it must contain"
                + " at least 1 element\"}");
  }

  @Test
  @DisplayName("選択装備データが取得できません")
  void failure_06() {
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
    monthlyPriceRes.getGrades()[0].getPackages()[0].setPackageEquipments(null);
    Mockito.when(carMasterGokuApi.getMonthlyPrice(any())).thenReturn(Mono.just(monthlyPriceRes));

    InsertMessageDataRes[] insertMessageDataRes =
        parseJsonFile(
            INPUT_ROOT + "/carModel/success_grade_list_data.json", new TypeReference<>() {});
    Mockito.when(carMasterGokuApi.getInsertMessageData())
        .thenReturn(Mono.just(insertMessageDataRes));

    webClient
        .get()
        .uri("/api/carModel/carModelId/1?planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .json(
            "{\"code\":500,\"message\":\"[Assertion failed] -"
                + " [パッケージ(id:PKG-0000005869)の選択装備データ(value: null)が取得できません] may not be null\"}");
  }

  @Test
  @DisplayName("排他装備データが取得できません")
  void failure_07() {
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
    monthlyPriceRes.getGrades()[0].getPackages()[0].setPackageExclusiveEquipments(null);
    Mockito.when(carMasterGokuApi.getMonthlyPrice(any())).thenReturn(Mono.just(monthlyPriceRes));

    InsertMessageDataRes[] insertMessageDataRes =
        parseJsonFile(
            INPUT_ROOT + "/carModel/success_grade_list_data.json", new TypeReference<>() {});
    Mockito.when(carMasterGokuApi.getInsertMessageData())
        .thenReturn(Mono.just(insertMessageDataRes));

    webClient
        .get()
        .uri("/api/carModel/carModelId/1?planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .json(
            "{\"code\":500,\"message\":\"[Assertion failed] -"
                + " [パッケージ(id:PKG-0000005869)の選択排他装備データ(value: null)が取得できません] may not be null\"}");
  }

  @Test
  @DisplayName("オプションデータが取得できません")
  void failure_08() {
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
    monthlyPriceRes.getGrades()[0].getPackages()[0].setSingleOptions(null);
    Mockito.when(carMasterGokuApi.getMonthlyPrice(any())).thenReturn(Mono.just(monthlyPriceRes));

    InsertMessageDataRes[] insertMessageDataRes =
        parseJsonFile(
            INPUT_ROOT + "/carModel/success_grade_list_data.json", new TypeReference<>() {});
    Mockito.when(carMasterGokuApi.getInsertMessageData())
        .thenReturn(Mono.just(insertMessageDataRes));

    webClient
        .get()
        .uri("/api/carModel/carModelId/1?planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError()
        .expectBody()
        .json(
            "{\"code\":500,\"message\":\"[Assertion failed] -"
                + " [パッケージ(id:PKG-0000005869)の選択オプションデータ(value: null)が取得できません] may not be null\"}");
  }
}
