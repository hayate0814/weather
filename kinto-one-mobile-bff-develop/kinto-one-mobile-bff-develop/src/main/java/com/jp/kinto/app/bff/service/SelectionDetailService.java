package com.jp.kinto.app.bff.service;

import static com.jp.kinto.app.bff.core.message.Msg.format;

import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.request.MonthlyPriceReq;
import com.jp.kinto.app.bff.model.carModel.CarModelResponse;
import com.jp.kinto.app.bff.model.carModel.CarModelResponse.FloorMat;
import com.jp.kinto.app.bff.utils.Asserts;
import java.util.Arrays;
import java.util.Comparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class SelectionDetailService {
  @Autowired private CarMasterGokuApi carMasterGokuApi;

  /**
   * 車両パーツ選択情報詳細
   *
   * @param monthlyPriceReq 条件
   * @return CurrentSelection
   */
  public Mono<CarModelResponse.CurrentSelection> get(MonthlyPriceReq monthlyPriceReq) {
    var rs = new CarModelResponse.CurrentSelection();
    return carMasterGokuApi
        .getMonthlyPrice(monthlyPriceReq) // 選択中パーツの月額利用料取得
        .map(
            monthlyPriceRes -> {
              Asserts.notNull(
                  monthlyPriceRes,
                  format("車種(id:{})の選択中パーツの月額利用料データが取得できません", monthlyPriceReq.getCarModelId()));
              Asserts.hasLength(
                  monthlyPriceRes.getGrades(),
                  format("車種(id:{})の選択グレートデータが取得できません", monthlyPriceReq.getCarModelId()));

              var grade = monthlyPriceRes.getGrades()[0];
              rs.setGrade(
                  new CarModelResponse.SelectionDetail(
                      grade.getId(),
                      grade.getMonthlyPrice(),
                      grade.getAdditionalApplicationCharge(),
                      grade.getName()));

              Asserts.hasLength(
                  grade.getOuterColors(), format("グレート(id:{})の選択外板色データが取得できません", grade.getId()));
              var outerColor = grade.getOuterColors()[0];
              rs.setOuterColor(
                  new CarModelResponse.SelectionDetail(
                      outerColor.getId(), outerColor.getMonthlyPrice(),
                      outerColor.getAdditionalApplicationCharge(), outerColor.getName()));

              Asserts.hasLength(
                  grade.getInteriorColors(), format("グレート(id:{})の選択内装色データが取得できません", grade.getId()));
              var interiorColor = grade.getInteriorColors()[0];
              var interiorSelectionDetail =
                  new CarModelResponse.SelectionDetail(
                      interiorColor.getId(), interiorColor.getMonthlyPrice(),
                      interiorColor.getAdditionalApplicationCharge(), interiorColor.getName());
              interiorSelectionDetail.setSeatColorName(interiorColor.getSeatColorName());
              var floorMat = new FloorMat(interiorColor.getFloorMat().getOfficialName());
              interiorSelectionDetail.setFloorMat(floorMat);
              rs.setInteriorColor(interiorSelectionDetail);

              Asserts.hasLength(
                  grade.getInteriorColors(),
                  format("グレート(id:{})の選択パッケージデータが取得できません", grade.getId()));
              var gradePackage = grade.getPackages()[0];
              rs.setGradePackage(
                  new CarModelResponse.SelectionDetail(
                      gradePackage.getId(),
                      gradePackage.getMonthlyPrice(),
                      gradePackage.getAdditionalApplicationCharge(),
                      gradePackage.getName()));

              Asserts.notNull(
                  gradePackage.getPackageEquipments(),
                  format("パッケージ(id:{})の選択装備データ(value: null)が取得できません", gradePackage.getId()));
              rs.setPackageEquipments(
                  Arrays.stream(gradePackage.getPackageEquipments())
                      .map(
                          pe ->
                              new CarModelResponse.PackageEquipmentDetail(
                                  pe.getId(), pe.getOfficialName()))
                      .toList());

              Asserts.notNull(
                  gradePackage.getPackageExclusiveEquipments(),
                  format("パッケージ(id:{})の選択排他装備データ(value: null)が取得できません", gradePackage.getId()));
              rs.setPackageExclusiveEquipments(
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
                  format("パッケージ(id:{})の選択オプションデータ(value: null)が取得できません", gradePackage.getId()));
              rs.setSingleOptions(
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

              boolean desiredNumberFlg =
                  monthlyPriceReq.getDesiredNumberPlateRequested() != null
                      && monthlyPriceReq.getDesiredNumberPlateRequested();
              rs.setDesiredNumberPlateRequest(
                  new CarModelResponse.DesiredNumberPlateRequest(
                      desiredNumberFlg,
                      monthlyPriceRes.getDesiredNumberPlatePrice(),
                      monthlyPriceRes.getDesiredNumberPlateAdditionalApplicationCharge()));

              rs.setPlanType(monthlyPriceRes.getPlanType());
              rs.setContractMonths(monthlyPriceRes.getContractMonths());
              rs.setBonusPayment(monthlyPriceRes.getBonusAmount());
              rs.setTotalMonthlyFee(monthlyPriceRes.getTotalMonthlyFee());
              if (Constant.PlanSv.PlanB.getCode().equals(monthlyPriceReq.getPlanType())) {
                rs.setTotalAdditionalApplicationCharge(
                    monthlyPriceRes.getAdditionalApplicationCharge());
              }
              rs.setCarModelImageURL(outerColor.getCarModelColorImageFile());
              return rs;
            });
  }
}
