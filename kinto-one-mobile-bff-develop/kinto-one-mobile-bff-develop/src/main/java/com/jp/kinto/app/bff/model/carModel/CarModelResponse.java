package com.jp.kinto.app.bff.model.carModel;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
public class CarModelResponse {
  private String makerCode;
  private String makerName;
  private String carModelCode;
  private String carModelId;
  private String carModelName;
  private String carModelEnglishName;
  private List<Integer> contractMonths;
  private List<Integer> bonusPayments;
  private Float taxRate;

  private InsertMessage insertMessage;

  private List<Grade> grades;
  private CurrentSelection currentSelection;

  @Data
  public static class CurrentSelection {
    private String planType;
    private Integer contractMonths;
    private Integer bonusPayment;
    private Integer totalMonthlyFee;
    private Integer totalAdditionalApplicationCharge;
    private String carModelImageURL;
    private SelectionDetail grade;
    private SelectionDetail outerColor;
    private SelectionDetail interiorColor;

    @JsonProperty("package")
    private SelectionDetail gradePackage;

    private List<SelectionDetail> packageExclusiveEquipments;
    private List<PackageEquipmentDetail> packageEquipments;
    private List<SingleOption> singleOptions;
    private DesiredNumberPlateRequest desiredNumberPlateRequest;
  }

  @Data
  @AllArgsConstructor
  public static class SelectionDetail {
    protected String id;
    protected Integer monthlyPrice;
    protected Integer additionalApplicationCharge;
    protected String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected String seatColorName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    protected FloorMat floorMat;

    public SelectionDetail(
        String id, Integer monthlyPrice, Integer additionalApplicationCharge, String name) {
      this.id = id;
      this.monthlyPrice = monthlyPrice;
      this.additionalApplicationCharge = additionalApplicationCharge;
      this.name = name;
    }
  }

  @Data
  @AllArgsConstructor
  public static class FloorMat {
    protected String name;
  }

  @Data
  @AllArgsConstructor
  public static class PackageEquipmentDetail {
    protected String id;
    protected String name;
  }

  @Data
  @AllArgsConstructor
  public static class EquipmentSelectionDetail {
    protected String id;
    protected String name;
  }

  @Data
  @AllArgsConstructor
  public static class DesiredNumberPlateRequest {
    private boolean selected;
    private Integer monthlyPrice;
    private Integer additionalApplicationCharge;
  }

  @Data
  @AllArgsConstructor
  public static class SingleOption {
    private String id;
    private Integer monthlyPrice;
    private Integer additionalApplicationCharge;
    private String name;
    private String groupName;
    private String abbreviation;
  }

  @Data
  @Builder(toBuilder = true)
  public static class Grade {
    private String gradeId;
    private String gradeCode;
    private String gradeName;
    private String description;
    private Integer boardingCapacity;
    private String hvGas;
    private String drive;
    private Integer displacement;
    private String recommendedGradeIconCode;
    private Integer taxExcludedMsrp;
  }

  @Data
  @Builder(toBuilder = true)
  public static class InsertMessage {
    private String insertMessageCarName;
    private String insertMessageGradeSelect;
    private String insertMessageGrade;
    private String insertMessageColorSelect;
    private String insertMessageStep1End;
    private String insertMessageOptionSelect;
    private String insertMessagePackage;
    private String insertMessageAddOption;
    private String insertMessageStep2End;
  }
}
