package com.jp.kinto.app.bff.model.contractRegist;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

@Data
public class ContractRegistReq {
  private String password;
  private String checkPassword;
  @JsonUnwrapped private ContractRegistRequest contractRegistRequest;
}
