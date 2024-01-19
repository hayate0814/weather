package com.jp.kinto.app.bff.model.dealer;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class YahooSearchResponse {
  private String address;
  private BigDecimal lat;
  private BigDecimal lon;
}
