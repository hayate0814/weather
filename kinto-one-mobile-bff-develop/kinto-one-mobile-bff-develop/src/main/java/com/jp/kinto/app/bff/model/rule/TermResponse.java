package com.jp.kinto.app.bff.model.rule;

import lombok.Data;

@Data
public class TermResponse {
  private Boolean checkbox;
  private String title;
  private String description;
  private String term;
}
