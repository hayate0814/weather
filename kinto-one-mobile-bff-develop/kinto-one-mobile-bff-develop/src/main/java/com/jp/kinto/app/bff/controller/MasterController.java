package com.jp.kinto.app.bff.controller;

import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.core.properties.AppProperties;
import com.jp.kinto.app.bff.goku.request.MonthlyPriceReq;
import com.jp.kinto.app.bff.goku.response.LexusDealerRes.LexusDealerDataRes;
import com.jp.kinto.app.bff.model.OuterPlateColorListResponse;
import com.jp.kinto.app.bff.model.appTerm.AppTermResponse;
import com.jp.kinto.app.bff.model.carModel.CarModelResponse;
import com.jp.kinto.app.bff.model.dealer.DealerResponse;
import com.jp.kinto.app.bff.model.dealer.YahooSearchResponse;
import com.jp.kinto.app.bff.model.interiorColer.InteriorColorListResponse;
import com.jp.kinto.app.bff.model.jsonData.Address;
import com.jp.kinto.app.bff.model.jsonData.CarInfoResponse;
import com.jp.kinto.app.bff.model.packageData.Package;
import com.jp.kinto.app.bff.model.rule.TermResponse;
import com.jp.kinto.app.bff.service.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping(Constant.API_URL_PREFIX)
public class MasterController {

  @Autowired private AppProperties appProperties;
  @Autowired private CarModelService carModelService;
  @Autowired private LineupService carMasterListService;
  @Autowired private SelectionDetailService selectionDetailService;
  @Autowired private OuterPlateColorListService outerPlateColorListService;
  @Autowired private PackageListService packageListService;
  @Autowired private AddressSearchService addressSearchService;
  @Autowired private InteriorColorListService interiorColorListService;
  @Autowired private DealerService dealerService;
  @Autowired private LexusDealerService lexusDealerService;
  @Autowired private TermService termService;
  @Autowired private AppTermService appTermService;
  @Autowired private PlaceNameSearchService placeNameSearchService;

  /**
   * LineUp画面用車種一覧情報取得API
   *
   * @param makerCode メーカーコード
   * @return レスポンス
   */
  @GetMapping("/lineup")
  public Mono<CarInfoResponse> lineup(String makerCode) {
    return carMasterListService.getLineupCarList(makerCode);
  }

  /**
   * 車種お見積り初期用データ取得
   *
   * @param carModelName 車種名・英字
   * @param planType 契約プラン
   * @return CarModelResponse
   */
  @GetMapping("/carModel/name/{name}")
  public Mono<CarModelResponse> getCarModelByName(
      @PathVariable("name") String carModelName, @RequestParam("planType") String planType) {
    return carModelService.getCarModelByName(carModelName, planType);
  }

  /**
   * 車種お見積り初期用データ取得
   *
   * @param carModelId 車種ID
   * @param planType 契約プラン
   * @return CarModelResponse
   */
  @GetMapping("/carModel/carModelId/{carModelId}")
  public Mono<CarModelResponse> getCarModelByCarModelId(
      @PathVariable("carModelId") String carModelId, @RequestParam("planType") String planType) {
    return carModelService.getCarModelByCarModelId(carModelId, planType);
  }

  @GetMapping("/selectionDetail")
  public Mono<CarModelResponse.CurrentSelection> selectionDetail(
      String carModelId,
      Integer contractMonths,
      Integer bonusAdditionAmount,
      String planType,
      Boolean desiredNumberPlateRequested,
      String gradeId,
      String packageId,
      String outerPlateColorId,
      String interiorColorId,
      String[] packageExclusiveEquipmentIds,
      String[] packageEquipmentIds,
      String[] singleOptionIds) {
    return selectionDetailService.get(
        MonthlyPriceReq.builder()
            .carModelId(carModelId)
            .contractMonths(contractMonths)
            .bonusAdditionAmount(bonusAdditionAmount)
            .planType(planType)
            .desiredNumberPlateRequested(desiredNumberPlateRequested)
            .gradeId(gradeId)
            .packageId(packageId)
            .outerPlateColorId(outerPlateColorId)
            .interiorColorId(interiorColorId)
            .packageExclusiveEquipmentIds(packageExclusiveEquipmentIds)
            .packageEquipmentIds(packageEquipmentIds)
            .singleOptionIds(singleOptionIds)
            .build());
  }

  /**
   * 外板色一覧取得API
   *
   * @param contractMonths 契約月数
   * @param gradeId グレードID
   * @param planType 契約プラン
   * @return レスポンス
   */
  @GetMapping("/outerPlateColorList")
  public Mono<List<OuterPlateColorListResponse>> outerPlateColorList(
      Integer contractMonths, String gradeId, String planType) {
    return outerPlateColorListService.getOuterPlateColorList(contractMonths, gradeId, planType);
  }

  /**
   * パッケージ一覧取得API
   *
   * @param contractMonths 契約月数
   * @param gradeId グレードID
   * @param planType 契約プラン
   * @return List<Package>
   */
  @GetMapping("/packageList")
  public Mono<List<Package>> packageList(Integer contractMonths, String gradeId, String planType) {
    return packageListService.getPackageList(contractMonths, gradeId, planType);
  }

  /**
   * 住所検索
   *
   * @param zipCode 郵便番号
   * @return List<Address>
   */
  @GetMapping("/addressSearch")
  public Mono<List<Address>> addressSearch(String zipCode) {
    return addressSearchService.getAddress(zipCode);
  }

  /**
   * 内装色一覧取得API
   *
   * @param contractMonths 契約月数
   * @param gradeId グレードID
   * @param planType 契約プラン
   * @return レスポンス
   */
  @GetMapping("/interiorColorList")
  public Mono<List<InteriorColorListResponse>> interiorColorList(
      Integer contractMonths, String gradeId, String planType) {
    return interiorColorListService.getInteriorColorList(contractMonths, gradeId, planType);
  }

  /**
   * 販売店一覧取得API
   *
   * @param carModelEnglishName 車種英語名
   * @param latitude 緯度
   * @param longitude 経度
   * @param keyword キーワード
   * @return List<DealerDataRes>
   */
  @GetMapping("/dealerList")
  public Mono<List<DealerResponse>> getDealers(
      String carModelEnglishName, BigDecimal latitude, BigDecimal longitude, String keyword) {
    return dealerService.getDealers(carModelEnglishName, latitude, longitude, keyword);
  }

  /**
   * レクサス販売店一覧取得API
   *
   * @param prefectureCode 都道府県コード
   * @param storeName 店舗名
   * @return List<LexusDealerDataRes>
   */
  @GetMapping("/lexusDealerList")
  public Mono<List<LexusDealerDataRes>> getLexusDealers(String prefectureCode, String storeName) {
    return lexusDealerService.getLexusDealers(prefectureCode, storeName);
  }

  /**
   * 利用規約取得
   *
   * @param isLexus レクサス
   * @param carModelEnglishName 車種英語名
   * @param planType プランタイプ
   * @param tconnect コネクティッド
   * @return List<TermResponse>
   */
  @GetMapping("/terms")
  public Mono<List<TermResponse>> getTermList(
      Boolean isLexus,
      String carModelEnglishName,
      String planType,
      String tconnect,
      @RequestParam(name = "isGr", defaultValue = "false") Boolean isGr,
      @RequestParam(name = "isMorizou", defaultValue = "false") Boolean isMorizou) {
    return termService.getTerms(isLexus, carModelEnglishName, planType, tconnect, isGr, isMorizou);
  }

  /**
   * アプリ起動時利用規約取得
   *
   * @return String
   */
  @GetMapping("/appTerm")
  public Mono<AppTermResponse> getAppTerm(ServerWebExchange exchange) {
    return Mono.just(
        new AppTermResponse(appProperties.getLatestTermVersion(), appTermService.getAppTerm()));
  }

  /**
   * 地名検索
   *
   * @return String
   */
  @GetMapping("/yahooSearch")
  public Mono<List<YahooSearchResponse>> getYahooSearch(String keyword) {
    return placeNameSearchService.getPlaceNameSearchDealers(keyword);
  }
}
