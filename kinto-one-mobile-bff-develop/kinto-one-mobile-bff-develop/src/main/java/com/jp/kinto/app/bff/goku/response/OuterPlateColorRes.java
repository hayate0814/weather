package com.jp.kinto.app.bff.goku.response;

import java.util.List;
import lombok.Data;

@Data
public class OuterPlateColorRes {
  private List<PlateColorData> data;

  @Data
  public static class PlateColorData {
    private String gradeCode;
    private String carModelCode;
    private String outerPlateColorAdditionalCode;
    private String carModelColorImageFile;
    private String displayOrder;
    private String contractMonths;
    private String tax;
    private String id;
    private String serviceStartedAt;
    private String serviceEndedAt;
    private String makerCode;
    private String englishName;
    private String code;
    private String name;
    private String recommendationComment;
    private String taxExcludedMsrp;
    private String paletteCode1;
    private String paletteCode2;
    private String taxRate;
    private String taxExcludedAdditionalApplicationCharge;
    private Integer additionalApplicationCharge;
    private String taxExcludedMonthlyPrice;
    private Integer monthlyPrice;
  }
}
