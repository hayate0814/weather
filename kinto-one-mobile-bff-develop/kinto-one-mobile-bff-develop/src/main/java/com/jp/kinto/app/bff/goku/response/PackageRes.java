package com.jp.kinto.app.bff.goku.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageRes {
  private List<PackageDataRes> data;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PackageDataRes {
    @JsonProperty("id")
    private String packageId;

    private String name;
    private List<String> displayNames;
    private String description;
    private String recommendationComment;
    private Integer additionalApplicationCharge;
    private Integer monthlyPrice;
    private List<PackageExclusiveEquipmentRes> packageExclusiveEquipments;
    private List<PackageEquipmentRes> packageEquipments;
    private List<SingleOptionRes> singleOptions;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PackageExclusiveEquipmentRes {
    private String groupCode;
    private String groupName;

    @JsonProperty("id")
    private String packageExclusiveEquipmentId;

    private String abbreviation;
    private Integer additionalApplicationCharge;
    private Integer differentialPrice;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PackageEquipmentRes {
    @JsonProperty("id")
    private String packageEquipmentId;

    private String abbreviation;
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class SingleOptionRes {
    @JsonProperty("id")
    private String singleOptionId;

    private String groupCode;
    private String groupName;
    private String abbreviation;
    private Integer additionalApplicationCharge;
    private Integer monthlyPrice;
    private Boolean isStandardEquipment;
  }
}
