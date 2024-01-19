package com.jp.kinto.app.bff.model.dealer;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class DealerResponse {
  private String dealerCode;
  private String vendorCode;
  private String storeCode;
  private String phoneNumber;
  private String address;
  private BigDecimal lat;
  private BigDecimal lon;
  private String url;
  private String dealerName;
  private Boolean isGrGarageSupported = false;
  private String grGarageStoreType;
  private String grGarageIconUrl;
}
