package com.jp.kinto.app.bff.goku.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class DealerRes {

  private List<DealerDataRes> data;

  @Data
  public static class DealerDataRes {
    private String dealerCode;
    private String vendorCode;
    private String storeCode;
    private String vendorName;
    private String storeName;
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
}
