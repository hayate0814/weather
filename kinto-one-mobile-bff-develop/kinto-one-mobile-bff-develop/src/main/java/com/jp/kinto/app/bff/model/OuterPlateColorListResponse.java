package com.jp.kinto.app.bff.model;

import lombok.Data;

@Data
public class OuterPlateColorListResponse {
  private String outerPlateColorId;
  private String carModelImageUrl;
  private String recommendedOuterPlateColorIconCode;
  private String outerPlateColorPaletteCode;
  private String outerPlateColorPaletteCode2;
  private Integer additionalApplicationCharge;
  private Integer monthlyPrice;
}
