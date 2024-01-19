package com.jp.kinto.app.bff.service;

import static com.jp.kinto.app.bff.core.constant.Constant.*;

import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.core.constant.Constant.BrandSv;
import com.jp.kinto.app.bff.core.exception.MessageException;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.core.properties.GokuProperties;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.response.CarDataRes;
import com.jp.kinto.app.bff.goku.response.DeliveryDateRes;
import com.jp.kinto.app.bff.model.jsonData.CarInfo;
import com.jp.kinto.app.bff.model.jsonData.CarInfoResponse;
import com.jp.kinto.app.bff.utils.Asserts;
import com.jp.kinto.app.bff.utils.Casts;
import com.jp.kinto.app.bff.utils.Checks;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

/**
 * LineUp画面表示用CarModelList取得 CarMasterListService
 *
 * @author YE-FOFU
 */
@Service
@Slf4j
public class LineupService {
  @Autowired private GokuProperties gokuProperties;
  @Autowired private CarMasterGokuApi carMasterGokuApi;

  /**
   * CarMasterList取得
   *
   * @return LineUp画面表示用情報
   */
  public Mono<CarInfoResponse> getLineupCarList(String makerCode) {
    log.debug("CarMasterList取得---START---");
    Asserts.notEmptyText(makerCode, () -> new MessageException(Msg.required.args("メーカーコード")));
    Asserts.assertTrue(
        Constant.BrandSv.isValidByCode(makerCode),
        () -> new MessageException(Msg.invalidValue.args("メーカーコード")));

    return Mono.zip(carMasterGokuApi.getCarDataJson(), carMasterGokuApi.getDeliveryData())
        .map(
            tuple -> {
              List<CarDataRes> carDataList = tuple.getT1();
              DeliveryDateRes deliveryData = tuple.getT2();

              Asserts.assertTrue(deliveryData.getData() != null, "deliveryData.jsonデータが取得失敗しました");

              Map<String, String> mapDeliveryDate =
                  deliveryData.getData().stream()
                      .collect(
                          Collectors.toMap(
                              DeliveryDateRes.Delivery::getCarModelCode,
                              DeliveryDateRes.Delivery::getDeliveryDateComment));

              List<CarInfo> carInfoList =
                  carDataList.stream()
                      .filter(
                          carDataItem ->
                              !BZ4X_CAR_MODEL_NAME.equals(carDataItem.getName())
                                  && Casts.toBoolean(carDataItem.getViewFlag())
                                  && Constant.BrandSv.getPrefixByCode(makerCode)
                                      .equals(carDataItem.getBrand()))
                      .map(
                          carDataItem ->
                              new CarInfo(
                                  carDataItem,
                                  makerCode,
                                  mapDeliveryDate.get(carDataItem.getCarModelCode()),
                                  gokuProperties.getUrl()))
                      .toList();

              List<CarInfo> sortCarInfoList = new ArrayList<>();
              Map<String, List<CarInfo>> carInfoMap =
                  carInfoList.stream()
                      .collect(
                          Collectors.groupingBy(
                              CarInfo::getCarType, LinkedHashMap::new, Collectors.toList()));

              if (BrandSv.Toyota.getCode().equals(makerCode)) {
                convertCarInfoListSort(TOYOTA_CAR_TYPE, carInfoMap, sortCarInfoList);
              } else {
                convertLexusCarInfoListSort(carInfoList, sortCarInfoList);
              }

              CarInfoResponse response = new CarInfoResponse();
              response.setCarList(sortCarInfoList);
              List<CarInfo> pickUpList =
                  carInfoList.stream()
                      .filter(
                          car ->
                              StringUtils.hasText(car.getRankingNow())
                                  && Checks.isNumeric(car.getRankingNow()))
                      .sorted(Comparator.comparing(car -> Integer.valueOf(car.getRankingNow())))
                      .collect(Collectors.toList());
              response.setPickupCarList(pickUpList);

              log.debug("CarMasterList取得---END---");
              return response;
            });
  }

  private void convertCarInfoListSort(
      List<String> carTypeList,
      Map<String, List<CarInfo>> carInfoMap,
      List<CarInfo> sortCarInfoList) {
    carTypeList.stream()
        .filter(carInfoMap::containsKey)
        .forEach(
            f -> {
              List<String> carModelName = new ArrayList<>();
              carInfoMap.get(f).stream()
                  .sorted(Comparator.comparing(CarInfo::getCarPrice))
                  .forEach(
                      g -> {
                        if (!carModelName.contains(g.getCarModelName())) {
                          carModelName.add(g.getCarModelName());
                        }
                      });
              carModelName.forEach(
                  h ->
                      sortCarInfoList.addAll(
                          carInfoMap.get(f).stream()
                              .collect(Collectors.groupingBy(CarInfo::getCarModelName))
                              .get(h)));
            });
  }

  private void convertLexusCarInfoListSort(
      List<CarInfo> carInfoList, List<CarInfo> sortCarInfoList) {
    // group by carModelName
    Map<String, List<CarInfo>> carNameMap =
        carInfoList.stream()
            .collect(
                Collectors.groupingBy(CarInfo::getCarModelName, HashMap::new, Collectors.toList()));
    // sort with carPrice
    carNameMap
        .keySet()
        .forEach(
            carName -> {
              carNameMap.get(carName).sort(Comparator.comparing(CarInfo::getCarPrice));
            });

    carNameMap.keySet().stream()
        .sorted(Comparator.comparing(name -> carNameMap.get(name).get(0).getCarPrice()))
        .forEach(
            carName -> {
              sortCarInfoList.addAll(carNameMap.get(carName));
            });
  }
}
