package com.jp.kinto.app.bff.model.simulation;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.ToString;

@Data
@ToString
public class SimulationRequest {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String updateDatetime;

  private SelectedCarSetting selectedCarSetting;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Personal personal;

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

    private Integer bonusAdditionAmount;

    private String packageId;

    private String[] packageExclusiveEquipmentIds;

    private String[] packageEquipmentIds;

    private String[] singleOptionIds;
  }

  @Data
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class Personal {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SelectedDealer selectedDealer;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Survey survey;

    private String lastName;

    private String firstName;

    private String lastNameKana;

    private String firstNameKana;

    private String birthday;

    private String gender;

    private String postalCode;

    private String prefecture;

    private String city;

    private String street;

    private String streetKana;

    private String addressOther;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UseLocation useLocation;

    private String email;

    private String phoneNumber;

    private String mobilePhoneNumber;

    private String preferredContactPhoneNumber;

    private List<String> preferredContactTimezones;

    private String residenceType;

    private String residenceYears;

    private String isPrimaryIncomeProvider;

    private String hasHousingLoan;

    private String hasSpouse;

    private Integer numberOfHousemates;

    private Integer numberOfChildren;

    private Integer annualIncome;

    private String usingPurpose;

    private String campaignCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Office office;
  }

  @Data
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class SelectedDealer {
    private String dealerCode;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ToyotaDealer toyotaDealer;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LexusDealer lexusDealer;
  }

  @Data
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class ToyotaDealer {
    private BigDecimal lat;
    private BigDecimal lon;
    private String carModelEnglishName;
  }

  @Data
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class LexusDealer {
    private String storeName;
  }

  @Data
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class Survey {

    private String ownedCarBrand;

    private String ownedCarCondition;
  }

  @Data
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class UseLocation {
    private String postalCode;

    private String prefecture;

    private String city;

    private String street;

    private String addressOther;

    private String cityKana;

    private String streetKana;

    private String selectedReason;
  }

  @Data
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class Office {

    private String job;

    private String workplaceBusinessStructure;

    private String workplaceBusinessStructureAffix;

    private String workplaceName;

    private String workplaceNameKana;

    private String workplaceDepartment;

    private String workplacePosition;

    private String workplacePostalCode;

    private String workplacePrefecture;

    private String workplaceCity;

    private String workplaceStreet;

    private String workplaceStreetKana;

    private String workplaceAddressOther;

    private String workplacePhoneNumber;

    private String workplaceHeadOfficeLocation;

    private String workplaceContinuousYears;

    private String workplaceNumberOfEmployees;

    @SneakyThrows
    public boolean hasAllAttributes() {
      for (Field field : this.getClass().getDeclaredFields()) {
        if (field.get(this) != null) {
          return true;
        }
      }
      return false;
    }
  }
}
