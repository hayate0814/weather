package com.jp.kinto.app.bff.packageList;

import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.exception.BadRequestException;
import com.jp.kinto.app.bff.core.exception.BadRequestException.ErrorCode;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.response.PackageRes;
import com.jp.kinto.app.bff.goku.response.PackageRes.PackageDataRes;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/packageList")
public class PackageTest extends ApiTest {

  @MockBean private CarMasterGokuApi gokuApi;

  @Test
  @DisplayName("packageList_success_planA")
  void packageList_success_planA() {
    PackageRes packageRes =
        PackageRes.builder()
            .data(
                Collections.singletonList(
                    PackageDataRes.builder()
                        .packageId("")
                        .packageEquipments(new ArrayList<>())
                        .packageExclusiveEquipments(new ArrayList<>())
                        .singleOptions(new ArrayList<>())
                        .build()))
            .build();
    Mockito.when(gokuApi.getPackageList(84, "GRD-0000005507", "PLAN_A"))
        .thenReturn(Mono.just(packageRes));
    webClient
        .get()
        .uri("/api/packageList?contractMonths=84&gradeId=GRD-0000005507&planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("packageList_success_planB")
  void packageList_success_planB() {
    PackageRes packageRes =
        PackageRes.builder()
            .data(
                Collections.singletonList(
                    PackageDataRes.builder()
                        .packageId("")
                        .packageEquipments(new ArrayList<>())
                        .packageExclusiveEquipments(new ArrayList<>())
                        .singleOptions(new ArrayList<>())
                        .build()))
            .build();
    Mockito.when(gokuApi.getPackageList(84, "GRD-0000005507", "PLAN_B"))
        .thenReturn(Mono.just(packageRes));
    webClient
        .get()
        .uri("/api/packageList?contractMonths=84&gradeId=GRD-0000005507&planType=PLAN_B")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("packageList_noContractMonth")
  void packageList_noContractMonth() {
    Mockito.when(gokuApi.getPackageList(null, "GRD-0000005507", "PLAN_A"))
        .thenThrow(new BadRequestException(ErrorCode.InputError, "契約月数は必須項目です。"));
    webClient
        .get()
        .uri("/api/packageList?gradeId=GRD-00000055073&planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
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
                .formatted(Msg.required.args("契約月数")));
  }

  @Test
  @DisplayName("packageList_noGradeId")
  void packageList_noGradeId() {
    Mockito.when(gokuApi.getPackageList(84, null, "PLAN_A"))
        .thenReturn(Mono.just(new PackageRes()));
    webClient
        .get()
        .uri("/api/packageList?contractMonths=84&planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
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
                .formatted(Msg.required.args("グレードID")));
  }

  @Test
  @DisplayName("packageList_noPlan")
  void packageList_noPlan() {
    Mockito.when(gokuApi.getPackageList(84, "GRD-0000005507", null))
        .thenReturn(Mono.just(new PackageRes()));
    webClient
        .get()
        .uri("/api/packageList?contractMonths=84&gradeId=GRD-00000055073")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
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
                .formatted(Msg.required.args("契約プラン")));
  }

  @Test
  @DisplayName("packageList_planError")
  void packageList_planError() {
    Mockito.when(gokuApi.getPackageList(84, "GRD-0000005507", "PLAN_C"))
        .thenReturn(Mono.just(new PackageRes()));
    webClient
        .get()
        .uri("/api/packageList?contractMonths=84&gradeId=GRD-0000005507&planType=PLAN_C")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
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
                .formatted(Msg.invalidValue.args("契約プラン")));
  }

  @Test
  @DisplayName("packageList_contractMonthsError")
  void packageList_contractMonthsError() {
    Mockito.when(gokuApi.getPackageList(85, "GRD-0000005507", "PLAN_A"))
        .thenReturn(Mono.just(new PackageRes()));
    webClient
        .get()
        .uri("/api/packageList?contractMonths=85&gradeId=GRD-0000005507&planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
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
                .formatted(Msg.invalidValue.args("契約月数")));
  }

  @Test
  @DisplayName("packageList_noData")
  void packageList_noData() {
    Mockito.when(gokuApi.getPackageList(84, "GRD-00000055073", "PLAN_A"))
        .thenThrow(mockGokuApiMessageExceptionWhenMasterApiCall(HttpStatus.NOT_FOUND, "C006"));
    webClient
        .get()
        .uri("/api/packageList?contractMonths=84&gradeId=GRD-00000055073&planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
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
  @DisplayName("packageList_unauthorized")
  void packageList_unauthorized() {
    Mockito.when(gokuApi.getPackageList(84, "GRD-0000005507", "PLAN_A"))
        .thenReturn(Mono.just(new PackageRes()));
    webClient
        .get()
        .uri("/api/packageList?contractMonths=84&gradeId=GRD-0000005507&planType=PLAN_B")
        .accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "")
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  @DisplayName("packageList_versionError")
  void packageList_versionError() {
    Mockito.when(gokuApi.getPackageList(84, "GRD-0000005507", "PLAN_A"))
        .thenReturn(Mono.just(new PackageRes()));
    webClient
        .get()
        .uri("/api/packageList?contractMonths=84&gradeId=GRD-0000005507&planType=PLAN_A")
        .accept(MediaType.APPLICATION_JSON)
        .header("Version", "")
        .exchange()
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
                .formatted(Msg.invalidValue.args("Versionヘッダー")));
  }
}
