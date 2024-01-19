package com.jp.kinto.app.bff.model.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class RefreshTokenRes {
  private String token;
}
