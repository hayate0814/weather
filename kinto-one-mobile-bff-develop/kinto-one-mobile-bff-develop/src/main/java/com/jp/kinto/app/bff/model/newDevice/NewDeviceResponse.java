package com.jp.kinto.app.bff.model.newDevice;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class NewDeviceResponse {
  private Integer userId;
  private String deviceCode;
  private String token;
}
