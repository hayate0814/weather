package com.jp.kinto.app.bff.model.contracts;

import lombok.Data;

@Data
public class ContractsListResponse {
  private Integer contractId;
  private String contractCarName;
  private String gradesName;
  private String carModelColorImageFile;
  private String applicationStatus;
  private String contractStartedAt;
  private String contractEndedAt;
  private String applicationDate;
  private String content;
  private String registNumber;
  private String vendorName;
  private String nextMaintenanceDate;
}
