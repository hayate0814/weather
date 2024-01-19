package com.jp.kinto.app.bff.goku.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeliveryDateRes {

  private List<Delivery> data;

  @Data
  @NoArgsConstructor
  @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Delivery {
    private String carModelCode;

    @JsonProperty("deliveryDateComment")
    private String deliveryDateComment;

    private String carModelEnglishName;
  }
}
