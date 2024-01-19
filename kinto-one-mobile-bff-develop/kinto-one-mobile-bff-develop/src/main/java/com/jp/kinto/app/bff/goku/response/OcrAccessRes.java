package com.jp.kinto.app.bff.goku.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OcrAccessRes {
  @JsonProperty("access_token")
  private String accessToken;
}
