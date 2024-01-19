package com.jp.kinto.app.bff.model.contractRegist;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Data;

@Data
public class ContractRegistRequest {
  private String memberType;
  private String userName;
  private Application application;
  private Survey survey;
  private DrivingLicense drivingLicense;
  private Contract contract;
  private ContractCar contractCar;
  private UseLocation useLocation;
  private PersonalContract personalContract;
  private Integer additionalApplicationCharge;
  private String campaignCode;

  @Data
  public static class Application {
    private String desiredNumberPlate1;
    private String desiredNumberPlate2;
    private String usingPurpose;
  }

  @Data
  public static class Survey {
    private String ownedCarBrand;
    private String ownedCarCondition;
  }

  @Data
  public static class DrivingLicense {
    private String number;
    private String imageFileFront;
    private String imageFileBack;
  }

  @Data
  public static class Contract {
    private String type;
    private String dealerPurchasedFrom;
    private Integer totalMonthlyFee;
  }

  @Data
  public static class ContractCar {
    private CarModelMonthlyFeeRequest carModelMonthlyFeeRequest;

    @Data
    public static class CarModelMonthlyFeeRequest {
      private String planType;
      private String salesChannel;
      private Integer contractMonths;
      private String carModelId;
      private String gradeId;
      private String outerPlateColorId;
      private String interiorColorId;
      private String packageId;
      private List<String> packageExclusiveEquipmentIds;
      private List<String> packageEquipmentIds;
      private List<String> singleOptionIds;
      private Integer bonusAdditionAmount;
    }
  }

  @Data
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
  public static class PersonalContract {
    private String lastName;
    private String firstName;
    private String lastNameKana;
    private String firstNameKana;
    private String gender;
    private String birthday;
    private String postalCode;
    private String prefecture;
    private String city;
    private String street;
    private String addressOther;
    private String cityKana;
    private String streetKana;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String phoneNumber;

    private String mobilePhoneNumber;
    private String preferredContactPhoneNumber;
    private List<String> preferredContactTimezones;
    private Integer annualIncome;
    private String residenceType;
    private String residenceYears;
    private Boolean hasHousingLoan;
    private Boolean isPrimaryIncomeProvider;
    private Boolean hasSpouse;
    private Integer numberOfHousemates;
    private Integer numberOfChildren;
    private String job;
    private String workplaceBusinessStructure;
    private String workplaceBusinessStructureAffix;
    private String workplaceName;
    private String workplaceNameKana;
    private String workplaceDepartment;
    private String workplacePosition;
    private String workplaceContinuousYears;
    private String workplaceHeadOfficeLocation;
    private String workplaceNumberOfEmployees;
    private String workplacePostalCode;
    private String workplacePrefecture;
    private String workplaceCity;
    private String workplaceStreet;
    private String workplaceAddressOther;
    private String workplaceCityKana;
    private String workplaceStreetKana;
    private String workplacePhoneNumber;
  }
}
