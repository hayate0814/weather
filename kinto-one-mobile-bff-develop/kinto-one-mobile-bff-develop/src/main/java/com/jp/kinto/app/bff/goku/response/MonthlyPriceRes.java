package com.jp.kinto.app.bff.goku.response;

import lombok.Data;

@Data
public class MonthlyPriceRes {
  private Object testDriveReservationURL;
  private String toyotaLexusURL;
  private Object pamphletURL;
  private Float tax;
  private String id;
  private String makerId;
  private String makerCode;
  private String makerName;
  private String code;
  private String name;
  private String englishName;
  private Object url;
  private Integer taxExcludedDesiredNumberPlatePrice;
  private Float taxRate;
  private Integer taxExcludedDesiredNumberPlateAdditionalApplicationCharge;
  private Integer desiredNumberPlateAdditionalApplicationCharge;
  private Integer desiredNumberPlatePrice;
  private Integer totalMonthlyFee;
  private Integer taxExcludedTotalMonthlyFee;
  private Integer additionalApplicationCharge;
  private Integer taxExcludedAdditionalApplicationCharge;
  private Integer bonusAmount;
  private String planType;
  private Integer contractMonths;
  private Grade[] grades;

  @Data
  public static class Grade {
    private String carModelCode;
    private Integer boardingCapacity;
    private String hvGas;
    private String certifiedModel;
    private String drive;
    private Integer displacement;
    private Object gasMileage;
    private Integer displayOrder;
    private String model;
    private String recommendedGradeIconCode;
    private Object specialSpecificationCarFlag;
    private Integer recyclingDeposit;
    private Object transmission;
    private Integer contractMonths;
    private Float tax;
    private String id;
    private String makerCode;
    private String englishName;
    private String code;
    private String name;
    private Integer taxExcludedMsrp;
    private String description;
    private Float taxRate;
    private Integer taxExcludedAdditionalApplicationCharge;
    private Integer additionalApplicationCharge;
    private Integer taxExcludedMonthlyPrice;
    private Integer monthlyPrice;
    private InteriorColor[] outerColors;
    private InteriorColor[] interiorColors;
    private InteriorColor[] packages;
  }

  @Data
  public static class InteriorColor {
    private String carModelCode;
    private String gradeCode;
    private Object interiorColorPaletteCode;
    private String interiorImageFile;
    private Integer displayOrder;
    private Integer contractMonths;
    private Float tax;
    private String id;
    private String makerCode;
    private String englishName;
    private String code;
    private String name;
    private String liningCode;
    private String seatColorName;
    private String recommendationComment;
    private Integer taxExcludedMsrp;
    private Float taxRate;
    private Integer taxExcludedAdditionalApplicationCharge;
    private Integer additionalApplicationCharge;
    private Integer taxExcludedMonthlyPrice;
    private Integer monthlyPrice;
    private InteriorOptionColor interiorOptionColor;
    private FloorMat floorMat;
    private String outerPlateColorAdditionalCode;
    private String carModelColorImageFile;
    private String paletteCode1;
    private Object paletteCode2;
    private String description;
    private Integer supportCarSubsidy;
    private PackageExclusiveEquipment[] packageExclusiveEquipments;
    private PackageEquipment[] packageEquipments;
    private PackageExclusiveEquipment[] singleOptions;
  }

  @Data
  public static class FloorMat {
    private String floorMatCode;
    private String interiorColorID;
    private Integer floorMatWage;
    private Integer contractMonths;
    private Float tax;
    private String abbreviation;
    private String officialName;
    private String accessoryInstallationOrderCode;
    private Integer taxExcludedMsrp;
  }

  @lombok.Data
  public static class InteriorOptionColor {
    private String interiorColorID;
    private Integer contractMonths;
    private Float tax;
    private Integer id;
    private Object name;
    private Object liningCode;
    private Integer taxExcludedMsrp;
  }

  @Data
  public static class PackageEquipment {
    private Integer displayOrder;
    private Integer contractMonths;
    private Float tax;
    private String id;
    private String makerCode;
    private String englishName;
    private String code;
    private String abbreviation;
    private String officialName;
    private String accessoryInstallationOrderCode;
    private String category;
    private Integer taxExcludedMsrp;
    private Integer wages;
  }

  @Data
  public static class PackageExclusiveEquipment {
    private Integer displayOrder;
    private Integer displayOrderInGroups;
    private Integer contractMonths;
    private Float tax;
    private String groupCode;
    private String groupName;
    private String id;
    private String makerCode;
    private String englishName;
    private String code;
    private String abbreviation;
    private String officialName;
    private String accessoryInstallationOrderCode;
    private String category;
    private Integer taxExcludedMsrp;
    private Integer wages;
    private Float taxRate;
    private Integer taxExcludedAdditionalApplicationCharge;
    private Integer additionalApplicationCharge;
    private Integer taxExcludedDifferentialPrice;
    private Integer differentialPrice;
    private String type;
    private Boolean isStandardEquipment;
    private Integer taxExcludedMonthlyPrice;
    private Integer monthlyPrice;

    public Integer getMonthlyPrice() {
      return (monthlyPrice != null) ? monthlyPrice : 0;
    }
  }
}
