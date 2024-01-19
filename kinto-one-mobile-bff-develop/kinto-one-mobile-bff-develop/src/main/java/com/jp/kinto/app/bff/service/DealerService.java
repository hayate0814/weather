package com.jp.kinto.app.bff.service;

import com.jp.kinto.app.bff.core.exception.MessageException;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.core.properties.GokuProperties;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.response.DealerRes;
import com.jp.kinto.app.bff.goku.response.DealerRes.DealerDataRes;
import com.jp.kinto.app.bff.model.dealer.DealerResponse;
import com.jp.kinto.app.bff.model.dealer.GrGarageStoreType;
import com.jp.kinto.app.bff.utils.Asserts;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class DealerService {
  @Autowired private CarMasterGokuApi carMasterGokuApi;
  @Autowired private GokuProperties gokuProperties;

  public Mono<List<DealerResponse>> getDealers(
      String carModelEnglishName, BigDecimal latitude, BigDecimal longitude, String keyword) {
    Asserts.notEmptyText(
        carModelEnglishName, () -> new MessageException(Msg.required.args("車種英語名")));
    if (latitude != null && longitude != null && keyword != null) {
      throw new MessageException(Msg.badRequest);
    }
    if (latitude != null && longitude == null) {
      Asserts.notNull(null, () -> new MessageException(Msg.required.args("経度")));
    }
    if (latitude == null && longitude != null) {
      Asserts.notNull(null, () -> new MessageException(Msg.required.args("緯度")));
    }
    Mono<DealerRes> dealerResMono;
    if (latitude != null) {
      dealerResMono = carMasterGokuApi.getDealerByRange(carModelEnglishName, latitude, longitude);
    } else {
      Asserts.notEmptyText(keyword, () -> new MessageException(Msg.required.args("検索のキーワード")));
      dealerResMono = carMasterGokuApi.getDealerByKey(carModelEnglishName, keyword);
    }

    return dealerResMono.map(
        dealerRes -> {
          List<DealerResponse> dealerList = new ArrayList<>();
          List<DealerDataRes> dealers = dealerRes.getData();
          for (DealerDataRes dealer : dealers) {
            DealerResponse dealerResponse = new DealerResponse();
            BeanUtils.copyProperties(dealer, dealerResponse);
            dealerResponse.setDealerName(
                String.join(" ", dealer.getVendorName(), dealer.getStoreName()));
            if (dealer.getIsGrGarageSupported()) {
              String grGarageStoreType = dealer.getGrGarageStoreType();
              String iconUrl = GrGarageStoreType.getIconUrlByCode(grGarageStoreType);
              dealerResponse.setGrGarageIconUrl(gokuProperties.getUrl() + iconUrl);
            }
            dealerList.add(dealerResponse);
          }
          return dealerList;
        });
  }
}
