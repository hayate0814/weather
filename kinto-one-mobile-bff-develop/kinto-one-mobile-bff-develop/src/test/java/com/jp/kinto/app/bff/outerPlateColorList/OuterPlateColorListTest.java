package com.jp.kinto.app.bff.outerPlateColorList;

import static org.mockito.ArgumentMatchers.any;

import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.response.OuterPlateColorRes;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/outerPlateColorList")
public class OuterPlateColorListTest extends ApiTest {

  @MockBean private CarMasterGokuApi gokuApi;

  @Test
  @DisplayName("入力チェック_契約月数")
  void fail_01() {

    webClient
        .get()
        .uri("/api/outerPlateColorList?gradeId=GRD-0000005656&planType=PLAN_A")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("""
                    {"code":1,"message":"契約月数は必須項目です"}
                    """);
  }

  @Test
  @DisplayName("入力チェック_グレードID")
  void fail_02() {

    webClient
        .get()
        .uri("/api/outerPlateColorList?contractMonths=36&planType=PLAN_A")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("""
                    {"code":1,"message":"グレードIDは必須項目です"}
                    """);
  }

  @Test
  @DisplayName("入力チェック_契約プラン")
  void fail_03() {

    webClient
        .get()
        .uri("/api/outerPlateColorList?contractMonths=36&gradeId=GRD-0000005656")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("""
                    {"code":1,"message":"契約プランは必須項目です"}
                    """);
  }

  @Test
  @DisplayName("整合性チェック_契約月数")
  void fail_4() {

    webClient
        .get()
        .uri("/api/outerPlateColorList?contractMonths=12&gradeId=GRD-0000005656&planType=PLAN_A")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json(
            """
                    {"code":1,"message":"契約月数の値が正しくありません"}
                    """);
  }

  @Test
  @DisplayName("整合性チェック_契約プラン")
  void fail_5() {

    webClient
        .get()
        .uri("/api/outerPlateColorList?contractMonths=12&gradeId=GRD-0000005656&planType=A")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json(
            """
                    {"code":1,"message":"契約プランの値が正しくありません"}
                    """);
  }

  @Test
  @DisplayName("外板色1件の場合")
  void outerPlateColorList_1件() {
    OuterPlateColorRes res = new OuterPlateColorRes();
    List<OuterPlateColorRes.PlateColorData> data = new ArrayList<>();
    OuterPlateColorRes.PlateColorData plateColorData = new OuterPlateColorRes.PlateColorData();
    plateColorData.setId("OPC-0000038079");
    plateColorData.setCarModelColorImageFile(
        "https://stg2-www.kinto-jp.com/s3resource/carmodel/ROM005X07.png");
    plateColorData.setRecommendationComment("人気No.2");
    plateColorData.setPaletteCode1("#ededf6");
    plateColorData.setPaletteCode2("#000000");
    plateColorData.setAdditionalApplicationCharge(0);
    plateColorData.setMonthlyPrice(440);
    data.add(plateColorData);
    res.setData(data);
    Mockito.when(
            (gokuApi.getOuterPlateColorList(
                any(Integer.class), any(String.class), any(String.class))))
        .thenReturn(Mono.just(res));

    webClient
        .get()
        .uri("/api/outerPlateColorList?contractMonths=36&gradeId=GRD-0000005656&planType=PLAN_A")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk()
        .expectBody()
        .json(
            """
                    [
                        {
                            "outerPlateColorId": "OPC-0000038079",
                            "carModelImageUrl": "https://stg2-www.kinto-jp.com/s3resource/carmodel/ROM005X07.png",
                            "recommendedOuterPlateColorIconCode": "人気No.2",
                            "outerPlateColorPaletteCode": "#ededf6",
                            "outerPlateColorPaletteCode2": "#000000",
                            "additionalApplicationCharge": 0,
                            "monthlyPrice": 440
                        }
                    ]
                     """);
  }

  @Test
  @DisplayName("外板色1件以上の場合")
  void outerPlateColorList_1件以上() {
    OuterPlateColorRes res = new OuterPlateColorRes();
    List<OuterPlateColorRes.PlateColorData> data = new ArrayList<>();
    OuterPlateColorRes.PlateColorData plateColorData1 = new OuterPlateColorRes.PlateColorData();
    plateColorData1.setId("OPC-0000038079");
    plateColorData1.setCarModelColorImageFile(
        "https://stg2-www.kinto-jp.com/s3resource/carmodel/ROM005X07.png");
    plateColorData1.setRecommendationComment("人気No.2");
    plateColorData1.setPaletteCode1("#ededf6");
    plateColorData1.setPaletteCode2("#000000");
    plateColorData1.setAdditionalApplicationCharge(0);
    plateColorData1.setMonthlyPrice(440);
    OuterPlateColorRes.PlateColorData plateColorData2 = new OuterPlateColorRes.PlateColorData();
    plateColorData2.setId("OPC-0000038086");
    plateColorData2.setCarModelColorImageFile(
        "https://stg2-www.kinto-jp.com/s3resource/carmodel/ROM005W24.png");
    plateColorData2.setRecommendationComment("人気No.1");
    plateColorData2.setPaletteCode1("#511603");
    plateColorData2.setPaletteCode2(null);
    plateColorData2.setAdditionalApplicationCharge(0);
    plateColorData2.setMonthlyPrice(0);
    data.add(plateColorData1);
    data.add(plateColorData2);
    res.setData(data);
    Mockito.when(
            (gokuApi.getOuterPlateColorList(
                any(Integer.class), any(String.class), any(String.class))))
        .thenReturn(Mono.just(res));

    webClient
        .get()
        .uri("/api/outerPlateColorList?contractMonths=36&gradeId=GRD-0000005656&planType=PLAN_A")
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk()
        .expectBody()
        .json(
            """
                    [
                        {
                            "outerPlateColorId": "OPC-0000038079",
                            "carModelImageUrl": "https://stg2-www.kinto-jp.com/s3resource/carmodel/ROM005X07.png",
                            "recommendedOuterPlateColorIconCode": "人気No.2",
                            "outerPlateColorPaletteCode": "#ededf6",
                            "outerPlateColorPaletteCode2": "#000000",
                            "additionalApplicationCharge": 0,
                            "monthlyPrice": 440
                        },
                        {
                            "outerPlateColorId": "OPC-0000038086",
                            "carModelImageUrl": "https://stg2-www.kinto-jp.com/s3resource/carmodel/ROM005W24.png",
                            "recommendedOuterPlateColorIconCode": "人気No.1",
                            "outerPlateColorPaletteCode": "#511603",
                            "outerPlateColorPaletteCode2": null,
                            "additionalApplicationCharge": 0,
                            "monthlyPrice": 0
                        }
                    ]
                     """);
  }
}
