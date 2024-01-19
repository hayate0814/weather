package com.jp.kinto.app.bff.goku.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class PlaceNameSearchDealersRes {
  private List<AddressInfos> addressInfos;

  @Data
  public static class AddressInfos {
    private String placeName;
    private Location location;
  }

  @Data
  public static class Location {
    private BigDecimal latitude;
    private BigDecimal longitude;
  }
}
