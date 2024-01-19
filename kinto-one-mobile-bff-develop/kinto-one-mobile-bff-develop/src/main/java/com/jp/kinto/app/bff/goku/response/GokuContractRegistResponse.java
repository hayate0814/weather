package com.jp.kinto.app.bff.goku.response;

import lombok.Data;

@Data
public class GokuContractRegistResponse {

  private Application application;
  private Contract contract;

  @Data
  public static class Application {
    private Integer id;
    private String status;
  }

  @Data
  public static class Contract {
    private Integer id;
    private String kintoCoreMemberId;
    private String status;
    private String oldContractId;
  }
}
