package com.jp.kinto.app.bff.goku.response;

import java.util.List;
import lombok.Data;

@Data
public class InteriorColorRes {
  private List<InteriorColorData> data;

  @Data
  public static class InteriorColorData {
    private String id;
    private String name;
    private String interiorImageFile;
    private String seatColorName;
    private Integer monthlyPrice;
    private Integer additionalApplicationCharge;
    private Abbreviation floorMat;
  }

  @Data
  public static class Abbreviation {
    private String abbreviation;
  }
}
