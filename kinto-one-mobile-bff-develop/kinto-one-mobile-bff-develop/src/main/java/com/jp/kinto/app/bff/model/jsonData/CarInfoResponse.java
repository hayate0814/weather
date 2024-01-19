package com.jp.kinto.app.bff.model.jsonData;

import java.util.List;
import lombok.Data;

@Data
public class CarInfoResponse {

  private List<CarInfo> carList;
  private List<CarInfo> pickupCarList;
}
