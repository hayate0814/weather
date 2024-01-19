package com.jp.kinto.app.bff.model.appTerm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppTermResponse {
  private String termVersion;
  private String content;
}
