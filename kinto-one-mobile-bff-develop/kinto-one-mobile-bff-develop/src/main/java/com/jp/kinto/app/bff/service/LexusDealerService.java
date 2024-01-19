package com.jp.kinto.app.bff.service;

import com.jp.kinto.app.bff.core.exception.MessageException;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.response.LexusDealerRes;
import com.jp.kinto.app.bff.goku.response.LexusDealerRes.LexusDealerDataRes;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class LexusDealerService {
  @Autowired private CarMasterGokuApi carMasterGokuApi;

  public Mono<List<LexusDealerDataRes>> getLexusDealers(String prefectureCode, String storeName) {
    if ((prefectureCode != null && storeName != null)
        || (prefectureCode == null && storeName == null)) {
      // リクエストのパラメータは正しくありません
      throw new MessageException(Msg.badRequest);
    }
    Mono<LexusDealerRes> dealerResMono;
    if (prefectureCode != null) {
      dealerResMono = carMasterGokuApi.getLexusDealerByPrefectureCode(prefectureCode);
    } else {
      dealerResMono = carMasterGokuApi.getLexusDealerByStoreName(storeName);
    }
    return dealerResMono.map(LexusDealerRes::getData);
  }
}
