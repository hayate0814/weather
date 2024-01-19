package com.jp.kinto.app.bff.model.jsonData;

import static com.jp.kinto.app.bff.core.constant.Constant.GR_SPORT_CAR_TYPE;
import static com.jp.kinto.app.bff.core.constant.Constant.TOYOTA_CAR_TYPE_EXCHANGE;
import static com.jp.kinto.app.bff.utils.Casts.toBoolean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jp.kinto.app.bff.goku.response.CarDataRes;
import lombok.Data;

@Data
public class CarInfo {

  private String makerCode;
  private String carModelName;
  private String carImageUrl;
  private String fuelLabel;
  private String specLabel;
  private String newProduct;
  @JsonIgnore private String rankingNow;
  private String serviceLabel;
  private String modelNote;
  private String carModelCode;
  private String specialCarType;
  private String carModelEnglishName;
  private Integer carPrice;
  private String delivery;
  private String carType;

  public CarInfo(
      CarDataRes carData, String makerCode, String deliveryDateComment, String imagePath) {
    this.makerCode = makerCode;
    this.carModelName = carData.getName();
    this.carImageUrl = imagePath + carData.getImagePath();
    this.fuelLabel = carData.getFuelLabel();
    this.specLabel = carData.getSpecLabel();
    this.newProduct = carData.getNewProduct();
    this.rankingNow = carData.getRankingNow();
    this.serviceLabel = carData.getServiceLabel();
    this.modelNote = carData.getModelNote();
    this.carModelCode = carData.getCarModelCode();
    this.specialCarType = carData.getSpecialCarType() != null ? carData.getSpecialCarType() : "";
    this.carModelEnglishName = carData.getCarModelEnglishName();
    this.carPrice = Integer.parseInt(carData.getYearmaxBonusmaxTaxin());
    this.delivery = deliveryDateComment == null ? "" : deliveryDateComment;
    this.carType =
        toBoolean(carData.getIsSortGrGrsport())
            ? GR_SPORT_CAR_TYPE
            : TOYOTA_CAR_TYPE_EXCHANGE.get(carData.getType());
  }
}
