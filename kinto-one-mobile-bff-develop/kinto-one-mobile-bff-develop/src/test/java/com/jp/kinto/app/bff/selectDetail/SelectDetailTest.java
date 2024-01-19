package com.jp.kinto.app.bff.selectDetail;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.request.MonthlyPriceReq;
import com.jp.kinto.app.bff.goku.response.MonthlyPriceRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/selectionDetail")
public class SelectDetailTest extends ApiTest {
  @MockBean private CarMasterGokuApi carMasterGokuApi;

  @Test
  @DisplayName("selectionDetail_success")
  void selectionDetail_success() {
    MonthlyPriceRes monthlyPriceRes =
        parseJsonFile(
            INPUT_ROOT + "/selection_detail/success_selection_detail.json",
            new TypeReference<>() {});

    MonthlyPriceReq request =
        MonthlyPriceReq.builder()
            .carModelId("CAR-0000001196")
            .contractMonths(84)
            .bonusAdditionAmount(100000)
            .planType("PLAN_A")
            .desiredNumberPlateRequested(true)
            .gradeId("GRD-0000005656")
            .packageId("PKG-0000021273")
            .outerPlateColorId("OPC-0000038079")
            .interiorColorId("ITC-0000007432")
            .packageExclusiveEquipmentIds(new String[] {"PED-0000009453", "PED-0000009455"})
            .packageEquipmentIds(new String[] {"PED-0000008435"})
            .singleOptionIds(
                new String[] {
                  "SOD-0000010778",
                  "SOD-0000010779",
                  "SOD-0000010780",
                  "SOD-0000010782",
                  "SOD-0000010783",
                  "SOD-0000010784",
                  "SOD-0000010785",
                  "SOD-0000010786"
                })
            .build();

    Mockito.when(carMasterGokuApi.getMonthlyPrice(request)).thenReturn(Mono.just(monthlyPriceRes));

    //    webClient.get()
    //        .uri(builder -> builder
    //            .path(String.format("/api/price/%s/%s/car-models/%s/%s", GOKU_API_VERSION,
    //                SALES_CHANNEL, request.getCarModelId(), request.getContractMonths()))
    //            .queryParams(toUriParamMap(request)).build())
    //        .accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isOk();
  }
}
