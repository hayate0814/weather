package com.jp.kinto.app.bff.model.interiorColer;

import lombok.Data;

@Data
public class InteriorColorListResponse {
  private String interiorId;
  private String interiorName;
  private String interiorImageUrl;
  private String seatColorName;
  private Integer monthlyPrice;
  private Integer additionalApplicationCharge;
  private String abbreviation;
}
