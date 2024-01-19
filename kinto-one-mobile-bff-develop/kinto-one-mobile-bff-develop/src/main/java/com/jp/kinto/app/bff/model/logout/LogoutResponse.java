package com.jp.kinto.app.bff.model.logout;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class LogoutResponse {
  private String token;
}
