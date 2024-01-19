package com.jp.kinto.app.bff.goku;

import static com.jp.kinto.app.bff.core.constant.Constant.*;
import static com.jp.kinto.app.bff.utils.Tips.toUriParamMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jp.kinto.app.bff.core.properties.GokuProperties;
import com.jp.kinto.app.bff.goku.client.GokuClient;
import com.jp.kinto.app.bff.goku.request.MonthlyPriceReq;
import com.jp.kinto.app.bff.goku.response.*;
import com.jp.kinto.app.bff.model.contractIncludeInfo.ContractIncludeInfoResponse.CarCharacteristics;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/** 車両マスタ関連API */
@Slf4j
@Component
public class CarMasterGokuApi {
  private GokuClient gokuClient;
  private GokuProperties gokuProperties;

  public CarMasterGokuApi(Function<String, WebClient> webClientFun, GokuProperties gokuProperties) {
    this.gokuProperties = gokuProperties;
    this.gokuClient = new GokuClient(webClientFun.apply(gokuProperties.getUrl()));
  }

  /**
   * cardata.jsonデータ取得
   *
   * @return Mono<List < CarData>>
   */
  public Mono<List<CarDataRes>> getCarDataJson() {
    return gokuClient
        .call(
            new ParameterizedTypeReference<byte[]>() {},
            client ->
                client
                    .get()
                    .uri(
                        gokuProperties.getCardataUrl()
                            + "/s3resource/s3data/jsondata/cardata.json"))
        .flatMap(
            data -> {
              try {
                List<CarDataRes> carData =
                    new ObjectMapper()
                        .readValue(
                            new String(data, StandardCharsets.UTF_8),
                            new TypeReference<List<CarDataRes>>() {});
                return Mono.just(carData);
              } catch (JsonProcessingException e) {
                throw new RuntimeException("JSONデータ変換失敗", e);
              }
            });
  }

  /**
   * deliverydate.jsonデータ取得
   *
   * @return Mono<DeliveryDate>
   */
  public Mono<DeliveryDateRes> getDeliveryData() {
    return gokuClient
        .call(
            new ParameterizedTypeReference<byte[]>() {},
            client ->
                client
                    .get()
                    .uri(
                        gokuProperties.getCardataUrl()
                            + "/s3resource/s3data/jsondata/deliverydate.json"))
        .flatMap(
            data -> {
              try {
                DeliveryDateRes deliveryDate =
                    new ObjectMapper()
                        .readValue(new String(data, StandardCharsets.UTF_8), DeliveryDateRes.class);
                return Mono.just(deliveryDate);
              } catch (JsonProcessingException e) {
                throw new RuntimeException("JSONデータ変換失敗", e);
              }
            });
  }

  /**
   * insert_message.jsonデータ取得
   *
   * @return Mono<InsertMessageDataRes[]>
   */
  public Mono<InsertMessageDataRes[]> getInsertMessageData() {
    return gokuClient
        .call(
            new ParameterizedTypeReference<byte[]>() {},
            client ->
                client
                    .get()
                    .uri(
                        gokuProperties.getInsertMessageUrl()
                            + "/s3resource/s3data/jsondata/insert_message.json"))
        .flatMap(
            data -> {
              try {
                InsertMessageDataRes[] insertMessageData =
                    new ObjectMapper()
                        .readValue(
                            new String(data, StandardCharsets.UTF_8), InsertMessageDataRes[].class);
                return Mono.just(insertMessageData);
              } catch (JsonProcessingException e) {
                throw new RuntimeException("JSONデータ変換失敗", e);
              }
            });
  }

  /**
   * 車種ID取得
   *
   * @param carModelName 車種名
   * @return {@link String}
   */
  public Mono<String> getCarModelId(String carModelName) {
    return gokuClient
        .call(
            Map.class,
            client ->
                client
                    .get()
                    .uri(
                        String.format(
                            "/api/price/%s/%s/car-models/name/%s",
                            GOKU_API_VERSION, SALES_CHANNEL, carModelName)))
        .map(d -> String.valueOf(d.get("id")));
  }

  /**
   * 車種別選択可能契約取得
   *
   * @param carModelId 車種ID
   * @return CarModelDetailRes.SelectableContract
   */
  public Mono<CarModelDetailRes.SelectableContract> getSelectableContract(String carModelId) {
    return gokuClient.call(
        CarModelDetailRes.SelectableContract.class,
        client ->
            client
                .get()
                .uri(
                    String.format(
                        "/api/price/%s/%s/car-models/%s/selectable-contract",
                        GOKU_API_VERSION, SALES_CHANNEL, carModelId)));
  }

  /**
   * グレード一覧取得
   *
   * @param contractMonths 契約月数
   * @param carModelId 車種ID
   * @return Grade[]
   */
  public Mono<CarModelDetailRes.Grade[]> getGrades(Integer contractMonths, String carModelId) {
    return gokuClient
        .call(
            CarModelDetailRes.GradesData.class,
            client ->
                client
                    .get()
                    .uri(
                        String.format(
                            "/api/price/%s/%s/%s/car-models/%s/grades/%s",
                            GOKU_API_VERSION, EXTERNAL, SALES_CHANNEL, carModelId, contractMonths)))
        .map(d -> d.getData());
  }

  /**
   * 選択中パーツの月額利用料取得
   *
   * @param request 取得条件
   * @return MonthlyPriceRes
   */
  public Mono<MonthlyPriceRes> getMonthlyPrice(MonthlyPriceReq request) {
    return gokuClient.call(
        MonthlyPriceRes.class,
        client ->
            client
                .get()
                .uri(
                    builder ->
                        builder
                            .path(
                                String.format(
                                    "/api/price/%s/%s/%s/car-models/%s/%s",
                                    GOKU_API_VERSION,
                                    EXTERNAL,
                                    SALES_CHANNEL,
                                    request.getCarModelId(),
                                    request.getContractMonths()))
                            .queryParams(toUriParamMap(request))
                            .build()));
  }

  /**
   * 外装色一覧取得
   *
   * @param contractMonths 契約月数
   * @param gradeId グレードID
   * @param planType 契約プラン
   * @return {@link OuterPlateColorRes}
   */
  public Mono<OuterPlateColorRes> getOuterPlateColorList(
      Integer contractMonths, String gradeId, String planType) {
    return gokuClient.call(
        OuterPlateColorRes.class,
        client ->
            client
                .get()
                .uri(
                    uriBuilder ->
                        uriBuilder
                            .path(
                                String.format(
                                    "/api/price/%s/%s/%s/grades/%s/outer-plate-colors/%s?",
                                    GOKU_API_VERSION,
                                    EXTERNAL,
                                    SALES_CHANNEL,
                                    gradeId,
                                    contractMonths))
                            .queryParam("contractMonths", contractMonths)
                            .queryParam("id", gradeId)
                            .queryParam("planType", planType)
                            .build()));
  }

  /**
   * パッケージ一覧取得
   *
   * @param contractMonths 契約月数
   * @param gradeId グレードID
   * @param planType 契約プラン
   * @return PackageResponse
   */
  public Mono<PackageRes> getPackageList(Integer contractMonths, String gradeId, String planType) {
    return gokuClient.call(
        PackageRes.class,
        client ->
            client
                .get()
                .uri(
                    String.format(
                        "/api/price/%s/%s/%s/grades/%s/packages/%s?planType=%s",
                        GOKU_API_VERSION,
                        EXTERNAL,
                        SALES_CHANNEL,
                        gradeId,
                        contractMonths,
                        planType)));
  }

  /**
   * フローマット取得
   *
   * @param contractMonths 契約月数
   * @param interiorColorId 内装色ID
   * @return FloorMatResponse
   */
  public Mono<FloorMatRes> getFloorMat(Integer contractMonths, String interiorColorId) {
    return gokuClient.call(
        FloorMatRes.class,
        client ->
            client
                .get()
                .uri(
                    String.format(
                        "/api/price/%s/%s/%s/interior-colors/%s/floor-mat/%s",
                        GOKU_API_VERSION,
                        EXTERNAL,
                        SALES_CHANNEL,
                        interiorColorId,
                        contractMonths)));
  }

  /**
   * 住所検索
   *
   * @param zipCode 郵便番号
   * @return FloorMatResponse
   */
  public Mono<AddressRes> getAddress(String zipCode) {
    return gokuClient.call(
        AddressRes.class,
        client ->
            client
                .get()
                .uri(String.format("/api/core/%s/address-search/%s", GOKU_API_VERSION, zipCode)));
  }

  /**
   * 内装色一覧取得
   *
   * @param contractMonths 契約月数
   * @param gradeId グレードID
   * @param planType 契約プラン
   * @return {@link InteriorColorRes}
   */
  public Mono<InteriorColorRes> getInteriorColorList(
      Integer contractMonths, String gradeId, String planType) {
    return gokuClient.call(
        InteriorColorRes.class,
        client ->
            client
                .get()
                .uri(
                    uriBuilder ->
                        uriBuilder
                            .path(
                                String.format(
                                    "/api/price/%s/%s/%s/grades/%s/interior-colors/%s?",
                                    GOKU_API_VERSION,
                                    EXTERNAL,
                                    SALES_CHANNEL,
                                    gradeId,
                                    contractMonths))
                            .queryParam("contractMonths", contractMonths)
                            .queryParam("id", gradeId)
                            .queryParam("planType", planType)
                            .build()));
  }

  /**
   * 位置情報に販売店情報取得
   *
   * @param carModelName 車種英語名
   * @param latitude 緯度
   * @param longitude 経度
   * @return DealerRes
   */
  public Mono<DealerRes> getDealerByRange(
      String carModelName, BigDecimal latitude, BigDecimal longitude) {
    return gokuClient.call(
        DealerRes.class,
        client ->
            client
                .get()
                .uri(
                    String.format(
                        "/api/dealers/%s?carModelEnglishName=%s&latitude=%s&longitude=%s",
                        GOKU_API_VERSION, carModelName, latitude, longitude)));
  }

  /**
   * keywordに販売店情報取得
   *
   * @param carModelName 車種英語名
   * @param keyword 検索のキーワード
   * @return DealerRes
   */
  public Mono<DealerRes> getDealerByKey(String carModelName, String keyword) {
    return gokuClient.call(
        DealerRes.class,
        client ->
            client
                .get()
                .uri(
                    String.format(
                        "/api/dealers/%s/%s/%s", GOKU_API_VERSION, carModelName, keyword)));
  }

  /**
   * 都道府県にてlexusの販売店情報取得
   *
   * @param prefectureCode 都道府県
   * @return LexusDealerRes
   */
  public Mono<LexusDealerRes> getLexusDealerByPrefectureCode(String prefectureCode) {
    return gokuClient.call(
        LexusDealerRes.class,
        client ->
            client
                .get()
                .uri(
                    String.format(
                        "/api/dealers/%s/prefecture/%s?prefectureCode=%s",
                        GOKU_API_VERSION, prefectureCode, prefectureCode)));
  }

  /**
   * 店舗名にてlexusの販売店情報取得
   *
   * @param storeName 店舗名
   * @return LexusDealerRes
   */
  public Mono<LexusDealerRes> getLexusDealerByStoreName(String storeName) {
    return gokuClient.call(
        LexusDealerRes.class,
        client ->
            client
                .get()
                .uri(
                    String.format(
                        "/api/dealers/%s/store/%s?storeName=%s",
                        GOKU_API_VERSION, storeName, storeName)));
  }

  /**
   * 車両特性情報取得
   *
   * @param carModelCode 契約月数
   * @param gradeCode グレードコード
   * @return PackageResponse
   */
  public Mono<List<CarCharacteristics>> getCharacteristics(String carModelCode, String gradeCode) {
    return gokuClient.call(
        new ParameterizedTypeReference<>() {},
        client ->
            client
                .get()
                .uri(
                    String.format(
                        "/api/price/%s/car-characteristics?carModelCode=%s&gradeCode=%s",
                        GOKU_API_VERSION, carModelCode, gradeCode)));
  }

  /**
   * 販売店情報取得
   *
   * @param dealerCode 販売店コード
   * @param isClosedStoresIncluded クローズ販売店
   * @return PackageResponse
   */
  public Mono<ContractDealerRes> getDealers(String dealerCode, Boolean isClosedStoresIncluded) {
    return gokuClient.call(
        ContractDealerRes.class,
        client ->
            client
                .get()
                .uri(
                    String.format(
                        "/api/dealers/%s/%s?dealerCode=%s&isClosedStoresIncluded=%s",
                        GOKU_API_VERSION, dealerCode, dealerCode, isClosedStoresIncluded)));
  }

  /**
   * 地名検索販売店情報取得
   *
   * @param placeName 地名
   * @return PackageResponse
   */
  public Mono<PlaceNameSearchDealersRes> getPlaceNameSearchDealers(String placeName) {
    return gokuClient.call(
        PlaceNameSearchDealersRes.class,
        client ->
            client
                .get()
                .uri(
                    String.format(
                        "/api/dealers/%s/address?keyword=%s", GOKU_API_VERSION, placeName)));
  }
}
