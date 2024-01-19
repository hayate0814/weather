package com.jp.kinto.app.bff.model.simulation;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class SimulationResponse {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String updateDatetime;

  private SimulationRequest.SelectedCarSetting selectedCarSetting;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private SimulationRequest.Personal personal;

  @Data
  public static class SelectedCarSetting {
    private String desiredNumberPlate1;

    private String desiredNumberPlate2;

    private String planType;

    private Integer contractMonths;

    private String carModelId;

    private String gradeId;

    private String outerPlateColorId;

    private String interiorColorId;

    private String packageId;

    private List<String> packageExclusiveEquipmentIds = new ArrayList<>();

    private List<String> packageEquipmentIds = new ArrayList<>();

    private List<String> singleOptionIds = new ArrayList<>();

    private Integer bonusAdditionAmount;
  }
}
