package com.jp.kinto.app.bff.goku.response;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class JpIdMemberInfoRes {
  private String id;
  private String type;
  private String email;
  private boolean mailActive;
  private boolean memberShip;
  private boolean editable;
  private PersonalMember personalMember;

  @Data
  public static class PersonalMember {
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
    private String phoneNumber;
    private String mobilePhoneNumber;
    private String preferredContactPhoneNumber;
    private List<String> preferredContactTimezones;
    private String residenceType;
    private String residenceYears;
    private String isPrimaryIncomeProvider;
    private String hasHousingLoan;
    private Integer annualIncome;
    private String hasSpouse;
    private Integer numberOfChildren;
    private Integer numberOfHousemates;
    private String streetKana;
    private Office office;
  }

  @Data
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
    private String workplaceAddressOther;
    private String workplaceCityKana;
    private String workplaceStreetKana;
    private String workplacePhoneNumber;
    private String workplaceHeadOfficeLocation;
    private String workplaceContinuousYears;
    private String workplaceNumberOfEmployees;
  }
}
