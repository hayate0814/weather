package com.jp.kinto.app.bff.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.core.exception.BadRequestException;
import com.jp.kinto.app.bff.core.exception.MessageException;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.request.MonthlyPriceReq;
import com.jp.kinto.app.bff.goku.response.MonthlyPriceRes;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import com.jp.kinto.app.bff.model.simulation.*;
import com.jp.kinto.app.bff.repository.SimulationRepository;
import com.jp.kinto.app.bff.repository.UserDeviceRepository;
import com.jp.kinto.app.bff.repository.entity.Simulation;
import com.jp.kinto.app.bff.repository.entity.UserDevice;
import com.jp.kinto.app.bff.utils.Asserts;
import com.jp.kinto.app.bff.utils.Casts;
import io.micrometer.common.util.StringUtils;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class SimulationService {

  @Autowired private CarMasterGokuApi carMasterGokuApi;

  @Autowired private SimulationRepository simulationRepository;

  @Autowired private UserDeviceRepository userDeviceRepository;

  protected static final ObjectMapper objectMapper = new ObjectMapper();

  public Flux<SimulationListResponse> getSimulationList(AuthUser authUserParam) {

    return Mono.just(authUserParam.getUserId())
        .flatMapMany(userId -> simulationRepository.findByGuestUserId(userId, 5))
        .map(
            simulation -> {
              Map<String, String> titleMap =
                  Casts.jsonToObject(simulation.getTitleJson(), new TypeReference<>() {});
              return SimulationListResponse.builder()
                  .simulationId(simulation.getSimulationId())
                  .planType(titleMap.getOrDefault("planType", ""))
                  .carModelId(titleMap.getOrDefault("carModelId", ""))
                  .carModelName(titleMap.getOrDefault("carModelName", ""))
                  .carModelEnglishName(titleMap.getOrDefault("carModelEnglishName", ""))
                  .gradeName(titleMap.getOrDefault("gradeName", ""))
                  .carImageUrl(titleMap.getOrDefault("carImageUrl", ""))
                  .simulationDatetime(
                      simulation
                          .getSimulationDatetime()
                          .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                  .updateDatetime(
                      simulation.getUpdateDatetime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                  .build();
            });
  }

  public Mono<SimulationResponse> getSimulation(AuthUser authUserParam, Integer simulationId) {
    return Mono.just(authUserParam.getUserId())
        .flatMap(
            userId ->
                simulationRepository
                    .findByIdAndGuestUserId(simulationId, userId)
                    .switchIfEmpty(
                        Mono.error(
                            new BadRequestException(
                                BadRequestException.ErrorCode.LogicMessage,
                                Msg.SimulationNotExist)))
                    .map(searchSimu -> searchSimu.setMemberId(null)))
        .flatMap(
            simulation -> {
              SimulationResponse simulationResponse =
                  Casts.jsonToObject(simulation.getSimulationJson(), new TypeReference<>() {});
              simulationResponse.setUpdateDatetime(
                  simulation.getUpdateDatetime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
              SimulationRequest.SelectedCarSetting selectedCar =
                  simulationResponse.getSelectedCarSetting();

              // gokuApi呼び出し
              return this.callGokuApi(selectedCar)
                  .map(
                      car -> {
                        selectedCar.setDesiredNumberPlate1(
                            Optional.ofNullable(selectedCar.getDesiredNumberPlate1()).orElse(""));
                        selectedCar.setDesiredNumberPlate2(
                            Optional.ofNullable(selectedCar.getDesiredNumberPlate2()).orElse(""));
                        selectedCar.setPackageExclusiveEquipmentIds(
                            Optional.ofNullable(selectedCar.getPackageExclusiveEquipmentIds())
                                .orElse(new String[0]));
                        selectedCar.setPackageEquipmentIds(
                            Optional.ofNullable(selectedCar.getPackageEquipmentIds())
                                .orElse(new String[0]));
                        selectedCar.setSingleOptionIds(
                            Optional.ofNullable(selectedCar.getSingleOptionIds())
                                .orElse(new String[0]));
                        return simulationResponse;
                      });
            });
  }

  @Transactional
  public Mono<Map<String, Object>> updateSimulation(
      AuthUser authUserParam, Integer simulationId, SimulationRequest simulationRequest) {
    // 入力チェック処理
    Asserts.notEmptyText(
        simulationRequest.getUpdateDatetime(),
        () -> new MessageException(Msg.required.args("更新日付")));
    this.validationCheck(simulationRequest);

    return Mono.just(authUserParam.getUserId())
        .flatMap(
            userId ->
                simulationRepository
                    .findByIdAndGuestUserId(simulationId, userId)
                    .switchIfEmpty(
                        Mono.error(
                            new BadRequestException(
                                BadRequestException.ErrorCode.LogicMessage,
                                Msg.SimulationNotExist))))
        .map(
            simulation -> {
              // シミレーションの排他チェック
              if (!simulation
                  .getUpdateDatetime()
                  .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                  .equals(simulationRequest.getUpdateDatetime())) {
                throw new BadRequestException(
                    BadRequestException.ErrorCode.LogicMessage, Msg.SimulationDataExpired);
              }
              return simulation;
            })
        .flatMap(
            simulation -> {
              // シミレーション更新する
              SimulationRequest.SelectedCarSetting selectedCar =
                  simulationRequest.getSelectedCarSetting();
              Mono<MonthlyPriceRes> gokuResMono = this.callGokuApi(selectedCar);

              return gokuResMono.flatMap(
                  gokuRes -> {
                    MonthlyPriceRes.Grade grade = gokuRes.getGrades()[0];
                    // シミレーション一覧情報作成
                    Map<String, Object> titleMap = new HashMap<>();
                    titleMap.put("planType", gokuRes.getPlanType());
                    titleMap.put("carModelId", gokuRes.getId());
                    titleMap.put("carModelName", gokuRes.getName());
                    titleMap.put("carModelEnglishName", gokuRes.getEnglishName());
                    titleMap.put("gradeName", grade.getName());
                    titleMap.put(
                        "carImageUrl", grade.getOuterColors()[0].getCarModelColorImageFile());

                    // システム日付
                    var now = LocalDateTime.now();
                    simulation
                        .setGuestUserId(authUserParam.getUserId())
                        .setTitleJson(Casts.toJson(objectMapper, titleMap))
                        .setSimulationJson(Casts.toJson(objectMapper, simulationRequest))
                        .setUpdateDatetime(now);

                    return simulationRepository.save(simulation);
                  });
            })
        .flatMap(
            simulationOpt ->
                simulationRepository
                    .findById(simulationOpt.getSimulationId())
                    .map(
                        searchSimu -> {
                          Map<String, Object> simulationIdMap = new HashMap<>();
                          simulationIdMap.put("simulationId", searchSimu.getSimulationId());
                          simulationIdMap.put(
                              "updateDatetime",
                              searchSimu
                                  .getUpdateDatetime()
                                  .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                          return simulationIdMap;
                        }));
  }

  @Transactional
  public Mono<Map<String, Object>> saveSimulation(
      AuthUser authUserParam, SimulationRequest simulationRequest) {
    // 入力チェック処理
    this.validationCheck(simulationRequest);

    SimulationRequest.SelectedCarSetting selectedCar = simulationRequest.getSelectedCarSetting();
    Mono<MonthlyPriceRes> gokuResMono = this.callGokuApi(selectedCar);
    return gokuResMono
        .flatMap(
            gokuRes -> {
              MonthlyPriceRes.Grade grade = gokuRes.getGrades()[0];
              // シミレーション一覧情報作成
              Map<String, Object> titleMap = new HashMap<>();
              titleMap.put("planType", gokuRes.getPlanType());
              titleMap.put("carModelId", gokuRes.getId());
              titleMap.put("carModelName", gokuRes.getName());
              titleMap.put("carModelEnglishName", gokuRes.getEnglishName());
              titleMap.put("gradeName", grade.getName());
              titleMap.put("carImageUrl", grade.getOuterColors()[0].getCarModelColorImageFile());

              return Mono.just(authUserParam.getUserId())
                  .flatMap(
                      userId -> {
                        UserDevice userDevice = new UserDevice();
                        // ユーザデバイス検索(メンバ情報取得)
                        if (Optional.ofNullable(authUserParam.getMemberId()).isEmpty()) {
                          return userDeviceRepository.findById(userId);
                        } else {
                          return Mono.just(userDevice.setMemberId(authUserParam.getMemberId()));
                        }
                      })
                  .flatMap(
                      userDevice -> {
                        // システム日付
                        var now = LocalDateTime.now();
                        // データ登録
                        return simulationRepository.save(
                            new Simulation()
                                .setGuestUserId(authUserParam.getUserId())
                                .setMemberId(userDevice.getMemberId())
                                .setSimulationDatetime(now)
                                .setTitleJson(Casts.toJson(objectMapper, titleMap))
                                .setSimulationJson(Casts.toJson(objectMapper, simulationRequest))
                                .setUpdateDatetime(now)
                                .setCreateDatetime(now));
                      });
            })
        .flatMap(
            simulation ->
                simulationRepository
                    .findById(simulation.getSimulationId())
                    .map(
                        searchSimu -> {
                          Map<String, Object> simulationIdMap = new HashMap<>();
                          simulationIdMap.put("simulationId", searchSimu.getSimulationId());
                          simulationIdMap.put(
                              "updateDatetime",
                              searchSimu
                                  .getUpdateDatetime()
                                  .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                          return simulationIdMap;
                        }));
  }

  @Transactional
  public Mono<Void> deleteSimulation(
      AuthUser authUserParam, Integer simulationId, String updateDatetime) {
    // 存在チェック
    Asserts.notEmptyText(updateDatetime, () -> new MessageException(Msg.required.args("更新日付")));
    return Mono.just(authUserParam.getUserId())
        .flatMap(
            userId ->
                simulationRepository
                    .findByIdAndGuestUserId(simulationId, userId)
                    .switchIfEmpty(
                        Mono.error(
                            new BadRequestException(
                                BadRequestException.ErrorCode.LogicMessage,
                                Msg.SimulationNotExist))))
        .flatMap(
            simulation -> {
              // シミレーションの排他チェック
              if (!simulation
                  .getUpdateDatetime()
                  .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                  .equals(updateDatetime)) {
                throw new BadRequestException(
                    BadRequestException.ErrorCode.LogicMessage, Msg.SimulationDataExpired);
              }
              return simulationRepository.deleteById(simulation.getSimulationId());
            });
  }

  private Mono<MonthlyPriceRes> callGokuApi(SimulationRequest.SelectedCarSetting selectedCar) {

    // 車両情報取得する
    Boolean numberPlate =
        StringUtils.isNotEmpty(selectedCar.getDesiredNumberPlate1())
            || StringUtils.isNotEmpty(selectedCar.getDesiredNumberPlate2());
    var carSetting =
        MonthlyPriceReq.builder()
            .carModelId(selectedCar.getCarModelId())
            .contractMonths(selectedCar.getContractMonths())
            .bonusAdditionAmount(selectedCar.getBonusAdditionAmount())
            .desiredNumberPlateRequested(numberPlate)
            .planType(selectedCar.getPlanType())
            .gradeId(selectedCar.getGradeId())
            .packageId(selectedCar.getPackageId())
            .outerPlateColorId(selectedCar.getOuterPlateColorId())
            .interiorColorId(selectedCar.getInteriorColorId())
            .packageExclusiveEquipmentIds(selectedCar.getPackageExclusiveEquipmentIds())
            .packageEquipmentIds(selectedCar.getPackageEquipmentIds())
            .singleOptionIds(selectedCar.getSingleOptionIds())
            .build();

    // gokuApi呼び出し
    return carMasterGokuApi.getMonthlyPrice(carSetting);
  }

  public void validationCheck(SimulationRequest simulationRequest) {
    Asserts.notNull(
        simulationRequest.getSelectedCarSetting(),
        () -> new MessageException(Msg.required.args("車両情報")));
    Asserts.notEmptyText(
        simulationRequest.getSelectedCarSetting().getPlanType(),
        () -> new MessageException(Msg.required.args("プランタイプ")));
    Asserts.notNull(
        simulationRequest.getSelectedCarSetting().getContractMonths(),
        () -> new MessageException(Msg.required.args("契約月数")));
    Asserts.notEmptyText(
        simulationRequest.getSelectedCarSetting().getCarModelId(),
        () -> new MessageException(Msg.required.args("車種ID")));
    Asserts.notEmptyText(
        simulationRequest.getSelectedCarSetting().getGradeId(),
        () -> new MessageException(Msg.required.args("グレードID")));
    Asserts.notEmptyText(
        simulationRequest.getSelectedCarSetting().getPackageId(),
        () -> new MessageException(Msg.required.args("パッケージID")));
    Asserts.notEmptyText(
        simulationRequest.getSelectedCarSetting().getOuterPlateColorId(),
        () -> new MessageException(Msg.required.args("外板色ID")));
    Asserts.notEmptyText(
        simulationRequest.getSelectedCarSetting().getInteriorColorId(),
        () -> new MessageException(Msg.required.args("内装色ID")));
    Asserts.notNull(
        simulationRequest.getSelectedCarSetting().getBonusAdditionAmount(),
        () -> new MessageException(Msg.required.args("ボーナス加算額")));
    Asserts.assertTrue(
        Constant.PlanSv.isValidByCode(simulationRequest.getSelectedCarSetting().getPlanType()),
        () -> new MessageException(Msg.invalidValue.args("契約プラン")));
    Asserts.isValidSystemValue(
        Constant.ContractMonthSv.class,
        simulationRequest.getSelectedCarSetting().getContractMonths(),
        () -> new MessageException(Msg.invalidValue.args("契約月数")));
  }
}
