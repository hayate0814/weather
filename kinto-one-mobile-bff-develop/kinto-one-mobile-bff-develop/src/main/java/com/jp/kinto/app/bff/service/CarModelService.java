package com.jp.kinto.app.bff.service;

import static com.jp.kinto.app.bff.core.message.Msg.format;

import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.core.exception.BadRequestException;
import com.jp.kinto.app.bff.core.exception.BadRequestException.ErrorCode;
import com.jp.kinto.app.bff.core.exception.GokuApiMessageException;
import com.jp.kinto.app.bff.core.exception.MessageException;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.core.properties.GokuProperties;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.request.MonthlyPriceReq;
import com.jp.kinto.app.bff.goku.response.CarModelDetailRes;
import com.jp.kinto.app.bff.goku.response.InsertMessageDataRes;
import com.jp.kinto.app.bff.goku.response.MonthlyPriceRes;
import com.jp.kinto.app.bff.model.carModel.CarModelResponse;
import com.jp.kinto.app.bff.model.carModel.CarModelResponse.FloorMat;
import com.jp.kinto.app.bff.utils.Asserts;
import com.jp.kinto.app.bff.utils.Tips;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class CarModelService {
  @Autowired private CarMasterGokuApi carMasterGokuApi;
  @Autowired private GokuProperties gokuProperties;
  private final Function<String, Consumer<? super Throwable>> onError =
      message ->
          e -> {
            throw new BadRequestException(ErrorCode.LogicMessage, message, e);
          };

  /**
   * 車種データ取得
   *
   * @param carModelName 車種名
   * @param planType 契約プラン
   * @return CarModelResponse
   */
  public Mono<CarModelResponse> getCarModelByName(String carModelName, String planType) {
    // 入力チェック
    Asserts.notEmptyText(carModelName, () -> new MessageException(Msg.required.args("車種名")));
    return carMasterGokuApi
        .getCarModelId(carModelName) // 車種ID取得
        .doOnError(
            GokuApiMessageException.class,
            e -> {
              if (HttpStatus.NOT_FOUND.equals(e.getRemoteHttpStatus())) {
                // goku から　データ取得できない場合
                throw new BadRequestException(ErrorCode.LogicMessage, Msg.CarNotExist, e);
              } else {
                throw e;
              }
            })
        .flatMap(carModelId -> getCarModelByCarModelId(carModelId, planType));
  }

  /**
   * 車種データ取得
   *
   * @param carModelId 車種ID
   * @param planType 契約プラン
   * @return CarModelResponse
   */
  public Mono<CarModelResponse> getCarModelByCarModelId(String carModelId, String planType) {
    // 入力チェック
    Asserts.notEmptyText(carModelId, () -> new MessageException(Msg.required.args("車種ID")));
    Asserts.assertTrue(
        Constant.PlanSv.isValidByCode(planType),
        () -> new MessageException(Msg.invalidValue.args("契約プラン")));

    var rs = new CarModelResponse();
    return carMasterGokuApi
        .getSelectableContract(carModelId)
        .flatMap(
            data -> {
              rs.setCarModelName(data.getName());
              rs.setCarModelCode(data.getCode());
              rs.setCarModelEnglishName(data.getEnglishName());
              rs.setCarModelId(data.getId());
              rs.setMakerCode(data.getMakerCode());
              rs.setMakerName(data.getMakerName());

              Integer contractMonths, bonusPayments;

              switch (Constant.PlanSv.fromString(planType)) {
                case PlanA -> {
                  rs.setContractMonths(Arrays.asList(data.getContractMonths()));
                  rs.setBonusPayments(Arrays.asList(data.getBonusPayments()));
                  contractMonths = Tips.max(data.getContractMonths());
                  bonusPayments = Tips.max(data.getBonusPayments());
                }
                case PlanB -> {
                  rs.setContractMonths(List.of(36));
                  rs.setBonusPayments(List.of(0));
                  contractMonths = 36;
                  bonusPayments = 0;
                }
                default -> throw new RuntimeException(
                    format("サポートされていない契約プラン. planType: {}", planType));
              }

              MonthlyPriceReq monthlyPriceReq =
                  MonthlyPriceReq.builder()
                      .carModelId(data.getId())
                      .contractMonths(contractMonths)
                      .bonusAdditionAmount(bonusPayments)
                      .planType(planType)
                      .build();

              return Mono.zip(
                      carMasterGokuApi.getGrades(contractMonths, data.getId()), // グレートデータ取得
                      carMasterGokuApi.getMonthlyPrice(monthlyPriceReq), // 選択中パーツの月額利用料取得
                      carMasterGokuApi.getInsertMessageData()) // インサートメッセージ取得
                  .map(
                      tuple -> {
                        CarModelDetailRes.Grade[] gradesRes = tuple.getT1();
                        Asserts.notNull(
                            gradesRes, format("車種(id:{})のグレートデータが取得できません", data.getId()));

                        InsertMessageDataRes[] insertMessageDataArr = tuple.getT3();
                        Asserts.notNull(
                            insertMessageDataArr, format("insert_message.jsonデータが取得失敗しました"));

                        Arrays.stream(insertMessageDataArr)
                            .filter(item -> data.getCode().equals(item.getCarModelCode()))
                            .findFirst()
                            .ifPresentOrElse(
                                res ->
                                    rs.setInsertMessage(
                                        CarModelResponse.InsertMessage.builder()
                                            .insertMessageCarName(
                                                getUrlReplenishment(res.getInsertMessageCarName()))
                                            .insertMessageGradeSelect(
                                                getUrlReplenishment(
                                                    res.getInsertMessageGradeSelect()))
                                            .insertMessagePackage(
                                                getUrlReplenishment(res.getInsertMessagePackage()))
                                            .insertMessageGrade(
                                                getUrlReplenishment(res.getInsertMessageGrade()))
                                            .insertMessageColorSelect(
                                                getUrlReplenishment(
                                                    res.getInsertMessageColorSelect()))
                                            .insertMessageStep1End(
                                                getUrlReplenishment(res.getInsertMessageStep1End()))
                                            .insertMessageOptionSelect(
                                                getUrlReplenishment(
                                                    res.getInsertMessageOptionSelect()))
                                            .insertMessageAddOption(
                                                getUrlReplenishment(
                                                    res.getInsertMessageAddOption()))
                                            .insertMessageStep2End(
                                                getUrlReplenishment(res.getInsertMessageStep2End()))
                                            .build()),
                                () ->
                                    rs.setInsertMessage(
                                        CarModelResponse.InsertMessage.builder()
                                            .insertMessageCarName("")
                                            .insertMessageGradeSelect("")
                                            .insertMessagePackage("")
                                            .insertMessageGrade("")
                                            .insertMessageColorSelect("")
                                            .insertMessageStep1End("")
                                            .insertMessageOptionSelect("")
                                            .insertMessageAddOption("")
                                            .insertMessageStep2End("")
                                            .build()));
                        rs.setGrades(
                            Arrays.stream(gradesRes)
                                .map(
                                    g ->
                                        CarModelResponse.Grade.builder()
                                            .gradeId(g.getId())
                                            .gradeCode(g.getCode())
                                            .drive(g.getDrive())
                                            .gradeName(g.getName())
                                            .description(g.getDescription())
                                            .displacement(g.getDisplacement())
                                            .hvGas(g.getHvGas())
                                            .boardingCapacity(g.getBoardingCapacity())
                                            .recommendedGradeIconCode(
                                                g.getRecommendedGradeIconCode())
                                            .taxExcludedMsrp(g.getTaxExcludedMsrp())
                                            .build())
                                .toList());
                        MonthlyPriceRes monthlyPriceRes = tuple.getT2();
                        Asserts.notNull(
                            monthlyPriceRes,
                            format("車種(id:{})の選択中パーツの月額利用料データが取得できません", data.getId()));
                        CarModelResponse.CurrentSelection currentSelection =
                            new CarModelResponse.CurrentSelection();

                        Asserts.hasLength(
                            monthlyPriceRes.getGrades(),
                            format("車種(id:{})の選択グレートデータが取得できません", data.getId()));
                        var grade = monthlyPriceRes.getGrades()[0];
                        currentSelection.setGrade(
                            new CarModelResponse.SelectionDetail(
                                grade.getId(),
                                grade.getMonthlyPrice(),
                                grade.getAdditionalApplicationCharge(),
                                grade.getName()));

                        Asserts.hasLength(
                            grade.getOuterColors(),
                            format("グレート(id:{})の選択外板色データが取得できません", grade.getId()));
                        var outerColor = grade.getOuterColors()[0];
                        currentSelection.setOuterColor(
                            new CarModelResponse.SelectionDetail(
                                outerColor.getId(), outerColor.getMonthlyPrice(),
                                outerColor.getAdditionalApplicationCharge(), outerColor.getName()));

                        Asserts.hasLength(
                            grade.getInteriorColors(),
                            format("グレート(id:{})の選択内装色データが取得できません", grade.getId()));
                        var interiorColor = grade.getInteriorColors()[0];
                        var interiorDetail =
                            new CarModelResponse.SelectionDetail(
                                interiorColor.getId(), interiorColor.getMonthlyPrice(),
                                interiorColor.getAdditionalApplicationCharge(),
                                    interiorColor.getName());
                        interiorDetail.setSeatColorName(interiorColor.getSeatColorName());
                        var floorMat = new FloorMat(interiorColor.getFloorMat().getOfficialName());
                        interiorDetail.setFloorMat(floorMat);
                        currentSelection.setInteriorColor(interiorDetail);

                        Asserts.hasLength(
                            grade.getPackages(),
                            format("グレート(id:{})の選択パッケージデータが取得できません", grade.getId()));
                        var gradePackage = grade.getPackages()[0];
                        currentSelection.setGradePackage(
                            new CarModelResponse.SelectionDetail(
                                gradePackage.getId(),
                                gradePackage.getMonthlyPrice(),
                                gradePackage.getAdditionalApplicationCharge(),
                                gradePackage.getName()));

                        Asserts.notNull(
                            gradePackage.getPackageEquipments(),
                            format(
                                "パッケージ(id:{})の選択装備データ(value: null)が取得できません", gradePackage.getId()));
                        currentSelection.setPackageEquipments(
                            Arrays.stream(gradePackage.getPackageEquipments())
                                .map(
                                    pe ->
                                        new CarModelResponse.PackageEquipmentDetail(
                                            pe.getId(), pe.getOfficialName()))
                                .toList());
                        Asserts.notNull(
                            gradePackage.getPackageExclusiveEquipments(),
                            format(
                                "パッケージ(id:{})の選択排他装備データ(value: null)が取得できません",
                                gradePackage.getId()));
                        currentSelection.setPackageExclusiveEquipments(
                            Arrays.stream(gradePackage.getPackageExclusiveEquipments())
                                .map(
                                    pe ->
                                        new CarModelResponse.SelectionDetail(
                                            pe.getId(),
                                            pe.getDifferentialPrice(),
                                            pe.getAdditionalApplicationCharge(),
                                            pe.getOfficialName()))
                                .toList());

                        Asserts.notNull(
                            gradePackage.getSingleOptions(),
                            format(
                                "パッケージ(id:{})の選択オプションデータ(value: null)が取得できません",
                                gradePackage.getId()));
                        currentSelection.setSingleOptions(
                            Arrays.stream(gradePackage.getSingleOptions())
                                .sorted(
                                    Comparator.comparingInt(
                                        so -> {
                                          return switch (so.getType()) {
                                            case "SINGLE_OPTION" -> 1;
                                            case "COLD_AREA_OPTION" -> 2;
                                            case "WINTER_TIRE_OPTION" -> 3;
                                            default -> 0;
                                          };
                                        }))
                                .map(
                                    so ->
                                        new CarModelResponse.SingleOption(
                                            so.getId(),
                                            so.getMonthlyPrice(),
                                            so.getAdditionalApplicationCharge(),
                                            so.getOfficialName(),
                                            so.getGroupName(),
                                            so.getAbbreviation()))
                                .toList());

                        currentSelection.setDesiredNumberPlateRequest(
                            new CarModelResponse.DesiredNumberPlateRequest(
                                false,
                                monthlyPriceRes.getDesiredNumberPlatePrice(),
                                monthlyPriceRes
                                    .getDesiredNumberPlateAdditionalApplicationCharge()));

                        currentSelection.setPlanType(monthlyPriceRes.getPlanType());
                        currentSelection.setContractMonths(monthlyPriceRes.getContractMonths());
                        currentSelection.setBonusPayment(monthlyPriceRes.getBonusAmount());
                        currentSelection.setTotalMonthlyFee(monthlyPriceRes.getTotalMonthlyFee());
                        if (Constant.PlanSv.PlanB.getCode().equals(planType)) {
                          currentSelection.setTotalAdditionalApplicationCharge(
                              monthlyPriceRes.getAdditionalApplicationCharge());
                        }
                        currentSelection.setCarModelImageURL(
                            outerColor.getCarModelColorImageFile());
                        rs.setCurrentSelection(currentSelection);
                        rs.setTaxRate(monthlyPriceRes.getTaxRate());

                        return rs;
                      });
            });
  }

  /**
   * getUrlReplenishment
   *
   * @param insertMessage 補充対象
   */
  private String getUrlReplenishment(String insertMessage) {
    return insertMessage.replace(
        "<a href='/", format("<a href='{}/", gokuProperties.getUrl().toString()));
  }
}
