package com.jp.kinto.app.bff.model.contractIncludeInfo;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractIncludeInfoResponse {

  private MonthlyIncludedContent monthlyIncludedContent;

  private CarCharacteristics carCharacteristics;

  @Data
  @Builder
  public static class MonthlyIncludedContent {
    private List<String> insurance;

    private List<String> taxFee;

    private List<String> maintenance;
    private List<String> connected;
  }

  @Data
  public static class CarCharacteristics {
    private Boolean isConnectedCarCare;
    private Boolean isExcludedProcare10;
    private Boolean isUnlimited;
    private Boolean isGr;
    private Boolean isMorizou;
    private Boolean isDigitalKey;
    private Boolean isForKintoCoreContract;
    private String tconnect;
  }
}
