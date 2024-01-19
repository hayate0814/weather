package com.jp.kinto.app.bff.model.simulation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimulationListResponse {

  private Integer simulationId;

  private String planType;

  private String carModelId;

  private String carModelName;

  private String carModelEnglishName;

  private String gradeName;

  private String carImageUrl;

  private String simulationDatetime;

  private String updateDatetime;
}
