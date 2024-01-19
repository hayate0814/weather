package com.jp.kinto.app.bff.model.jsonData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {
  private String zipCode;
  private String prefecture;
  private String city;
  private String town;
  private String prefectureKana;
  private String cityKana;
  private String townKana;
}
