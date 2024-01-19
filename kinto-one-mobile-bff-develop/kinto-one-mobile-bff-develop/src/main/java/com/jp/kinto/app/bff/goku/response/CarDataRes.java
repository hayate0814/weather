package com.jp.kinto.app.bff.goku.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CarDataRes {
  private String name;
  private String type;
  private String brand;

  @JsonProperty("toDate")
  private String toDate;

  private String fromDate;

  @JsonProperty("tradeFlg")
  private String tradeFlg;

  @JsonProperty("imagePath")
  private String imagePath;

  private String fuelLabel;
  private String specLabel;
  private String newProduct;
  private String rankingNow;
  private String serviceLabel;
  private String modelNote;
  private String carDetailUrl;
  private String carModelCode;

  @JsonProperty("specialCarType")
  private String specialCarType;

  @JsonProperty("viewFlag")
  private String viewFlag;

  @JsonProperty("sort_gr-grsport")
  private String isSortGrGrsport;

  private String carModelEnglishName;
  private String outerPlateColorCode;
  private String yearmaxBonusmaxTaxin;
}
