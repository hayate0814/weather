package com.jp.kinto.app.bff.goku.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class MonthlyPriceReq {
  private String carModelId;
  private Integer contractMonths;
  private Integer bonusAdditionAmount;
  private String planType;
  private Boolean desiredNumberPlateRequested;
  private String gradeId;
  private String packageId;
  private String outerPlateColorId;
  private String interiorColorId;
  private String[] packageExclusiveEquipmentIds;
  private String[] packageEquipmentIds;
  private String[] singleOptionIds;
}
