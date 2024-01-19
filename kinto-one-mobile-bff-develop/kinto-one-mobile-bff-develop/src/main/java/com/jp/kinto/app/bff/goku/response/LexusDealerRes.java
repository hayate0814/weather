package com.jp.kinto.app.bff.goku.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class LexusDealerRes {

  private List<LexusDealerDataRes> data;

  @Data
  public static class LexusDealerDataRes {
    private String dealerCode;
    private String vendorCode;
    private String storeCode;
    private String storeName;
    private String phoneNumber;
    private String address;
    private BigDecimal lat;
    private BigDecimal lon;
    private String url;
    private String hours;
    private String holiday;
    private String dealerName;
    private String storeImageFileName;
  }
}
