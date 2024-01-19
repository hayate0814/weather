package com.jp.kinto.app.bff.interiorColor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.exception.BadRequestException;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.response.InteriorColorRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/interiorColorList")
public class InteriorColorTest extends ApiTest {

  @MockBean private CarMasterGokuApi carMasterGokuApi;

  @Test
  @DisplayName("interiorColorList_success_planA")
  void interiorColorList_success_planA() {
    InteriorColorRes interiorColorRes =
        parseJsonFile(
            INPUT_ROOT + "/interior_color/success_interior_color.json", new TypeReference<>() {});

    Mockito.when(carMasterGokuApi.getInteriorColorList(84, "GRD-0000005656", "PLAN_A"))
        .thenReturn(Mono.just(interiorColorRes));

    webClient
        .get()
        .uri("/api/interiorColorList?contractMonths=84&gradeId=GRD-0000005656&planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("interiorColorList_success_planB")
  void interiorColorList_success_planB() {
    InteriorColorRes interiorColorRes =
        parseJsonFile(
            INPUT_ROOT + "/interior_color/success_interior_color.json", new TypeReference<>() {});

    Mockito.when(carMasterGokuApi.getInteriorColorList(84, "GRD-0000005656", "PLAN_B"))
        .thenReturn(Mono.just(interiorColorRes));

    webClient
        .get()
        .uri("/api/interiorColorList?contractMonths=84&gradeId=GRD-0000005656&planType=PLAN_B")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("interiorColorList_noContractMonth")
  void interiorColorList_noContractMonth() {
    Mockito.when(carMasterGokuApi.getInteriorColorList(null, "GRD-0000005507", "PLAN_A"))
        .thenThrow(
            new BadRequestException(BadRequestException.ErrorCode.InputError, "契約月数は必須項目です。"));
    webClient
        .get()
        .uri("/api/interiorColorList?gradeId=GRD-0000005656&planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("{\"code\":1,\"message\":\"契約月数は必須項目です\"}");
  }

  @Test
  @DisplayName("interiorColorList_noGradeId")
  void interiorColorList_noGradeId() {
    Mockito.when(carMasterGokuApi.getInteriorColorList(84, null, "PLAN_A"))
        .thenThrow(
            new BadRequestException(BadRequestException.ErrorCode.InputError, "グレードIDは必須項目です。"));
    webClient
        .get()
        .uri("/api/interiorColorList?contractMonths=84&planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("{\"code\":1,\"message\":\"グレードIDは必須項目です\"}");
  }

  @Test
  @DisplayName("interiorColorList_noPlan")
  void interiorColorList_noPlan() {
    Mockito.when(carMasterGokuApi.getInteriorColorList(84, "GRD-0000005656", null))
        .thenThrow(
            new BadRequestException(BadRequestException.ErrorCode.InputError, "契約プランは必須項目です。"));
    webClient
        .get()
        .uri("/api/interiorColorList?contractMonths=84&gradeId=GRD-0000005656")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("{\"code\":1,\"message\":\"契約プランは必須項目です\"}");
  }

  @Test
  @DisplayName("interiorColorList_errPlan")
  void interiorColorList_errPlan() {
    Mockito.when(carMasterGokuApi.getInteriorColorList(84, "GRD-0000005656", "PLAN_C"))
        .thenThrow(
            new BadRequestException(BadRequestException.ErrorCode.InputError, "契約プランの値が正しくありません"));
    webClient
        .get()
        .uri("/api/interiorColorList?contractMonths=84&gradeId=GRD-0000005656&planType=PLAN_C")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("{\"code\":1,\"message\":\"契約プランの値が正しくありません\"}");
  }

  @Test
  @DisplayName("interiorColorList_errContractMonth")
  void interiorColorList_errContractMonth() {
    Mockito.when(carMasterGokuApi.getInteriorColorList(100, "GRD-0000005507", "PLAN_A"))
        .thenThrow(
            new BadRequestException(BadRequestException.ErrorCode.InputError, "契約月数の値が正しくありません"));
    webClient
        .get()
        .uri("/api/interiorColorList?contractMonths=100&gradeId=GRD-0000005656&planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("{\"code\":1,\"message\":\"契約月数の値が正しくありません\"}");
  }
}
