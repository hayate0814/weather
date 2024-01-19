package com.jp.kinto.app.bff.goku.response;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ContractsRes {
  private Integer id;
  private Application application;
  private ContractCar contractCar;
  private String status;
  private String contractDate;
  private String expirationDate;

  @Data
  public static class Application {
    private Integer id;
    private String status;
    private String requestedAt;
    private String type;
  }

  @Data
  public static class ContractCar {
    private String registrationNumberArea;
    private String registrationNumberClass;
    private String registrationNumberKana;
    private String registrationNumberSpecified;
    private SelectedCarSettings selectedCarSettings;
    private CarMaintenances[] carMaintenances;
    private MaintenanceDealer maintenanceDealer;
  }

  @Data
  public static class MaintenanceDealer {
    private String code;
  }

  @Data
  public static class SelectedCarSettings {
    private String serviceStartedAt;
    private String serviceEndedAt;
    private String name;
    private String makerCode;
    private Grade[] grades;
  }

  @Data
  public static class CarMaintenances {
    private Integer id;
    private String type;
    private String label;
    private LocalDate expectedDate;
  }

  @Data
  public static class Grade {
    private String name;
    private OuterColor[] outerColors;
  }

  @Data
  public static class OuterColor {
    private String carModelColorImageFile;
  }
}
