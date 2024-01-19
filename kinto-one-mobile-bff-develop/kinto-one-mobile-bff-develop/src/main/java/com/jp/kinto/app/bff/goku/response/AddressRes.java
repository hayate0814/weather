package com.jp.kinto.app.bff.goku.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jp.kinto.app.bff.model.jsonData.Address;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class AddressRes {

  @JsonProperty("address")
  private List<Address> addresses = new ArrayList<>();
}
