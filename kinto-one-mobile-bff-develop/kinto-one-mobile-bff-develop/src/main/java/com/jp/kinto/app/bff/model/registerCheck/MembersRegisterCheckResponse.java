package com.jp.kinto.app.bff.model.registerCheck;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class MembersRegisterCheckResponse {
  private Boolean register;
}
