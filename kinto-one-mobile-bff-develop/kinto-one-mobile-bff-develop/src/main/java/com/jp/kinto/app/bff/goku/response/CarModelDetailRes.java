package com.jp.kinto.app.bff.goku.response;

import lombok.Data;

public class CarModelDetailRes {

  @Data
  public static class SelectableContract {
    private String id;
    private String makerCode;
    private String makerName;
    private String code;
    private String name;
    private String englishName;
    private Integer[] contractMonths;
    private Integer[] bonusPayments;
  }

  @Data
  public static class GradesData {
    private Grade[] data;
  }

  @Data
  public static class Grade {
    private String carModelCode;
    private Integer boardingCapacity;
    private String hvGas;
    private String certifiedModel;
    private String drive;
    private Integer displacement;
    private String gasMileage;
    private Integer displayOrder;
    private String model;
    private String recommendedGradeIconCode;
    private String specialSpecificationCarFlag;
    private Integer recyclingDeposit;
    private String transmission;
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
  }
}
