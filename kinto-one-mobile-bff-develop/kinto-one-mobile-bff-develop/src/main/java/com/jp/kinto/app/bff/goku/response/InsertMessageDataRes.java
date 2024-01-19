package com.jp.kinto.app.bff.goku.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InsertMessageDataRes {
  private String carModelCode;
  private String insertMessageCarName;
  private String insertMessageGradeSelect;
  private String insertMessageGrade;
  private String insertMessageColorSelect;
  private String insertMessageStep1End;
  private String insertMessageOptionSelect;
  private String insertMessagePackage;
  private String insertMessageAddOption;
  private String insertMessageStep2End;
}
