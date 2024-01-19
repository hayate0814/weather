package com.jp.kinto.app.bff.service;

import com.jp.kinto.app.bff.core.exception.MessageException;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.model.dealer.YahooSearchResponse;
import com.jp.kinto.app.bff.utils.Asserts;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 地名検索 PlaceNameSearchService
 *
 * @return 販売店一覧情報
 */
@Service
@Slf4j
public class PlaceNameSearchService {

  @Autowired private CarMasterGokuApi gokuApi;

  public Mono<List<YahooSearchResponse>> getPlaceNameSearchDealers(String keyword) {
    Asserts.notEmptyText(keyword, () -> new MessageException(Msg.required.args("検索のキーワード")));

    return gokuApi
        .getPlaceNameSearchDealers(keyword)
        .map(
            data -> {
              Asserts.assertTrue(data != null && data.getAddressInfos() != null, "販売店データが取得失敗しました");
              return data.getAddressInfos().stream()
                  .map(
                      f -> {
                        var response = new YahooSearchResponse();
                        response.setAddress(f.getPlaceName());
                        response.setLat(f.getLocation().getLatitude());
                        response.setLon(f.getLocation().getLongitude());
                        return response;
                      })
                  .toList();
            });
  }
}
