package com.jp.kinto.app.bff.model.packageData;

import java.util.List;
import lombok.Data;

@Data
public class Package {
  private String packageId;
  private String name;
  private List<String> displayNames;
  private String description;
  private String recommendationComment;
  private Integer additionalApplicationCharge;
  private Integer monthlyPrice;
  private List<PackageExclusiveEquipment> packageExclusiveEquipments;
  private List<PackageEquipment> packageEquipments;
  private List<SingleOption> singleOptions;

  @Data
  public static class PackageEquipment {
    private String packageEquipmentId;
    private String abbreviation;
  }

  @Data
  public static class PackageExclusiveEquipment {
    private String groupCode;
    private String groupName;
    private String packageExclusiveEquipmentId;
    private String abbreviation;
    private Integer additionalApplicationCharge;
    private Integer differentialPrice;
  }

  @Data
  public static class SingleOption {
    private String singleOptionId;
    private String groupCode;
    private String groupName;
    private String abbreviation;
    private Integer additionalApplicationCharge;
    private Integer monthlyPrice;
    private Boolean isStandardEquipment;
  }
}
