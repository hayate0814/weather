package com.jp.kinto.app.bff.service;

import static com.jp.kinto.app.bff.core.constant.Constant.NUMBER_REGULAR;
import static com.jp.kinto.app.bff.core.constant.Constant.ZIP_CODE_SIZE;

import com.jp.kinto.app.bff.core.exception.GokuApiMessageException;
import com.jp.kinto.app.bff.core.exception.MessageException;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.response.AddressRes;
import com.jp.kinto.app.bff.model.jsonData.Address;
import com.jp.kinto.app.bff.utils.Asserts;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AddressSearchService {
  @Autowired private CarMasterGokuApi carMasterGokuApi;

  public Mono<List<Address>> getAddress(String zipCode) {
    // 入力チェック
    Asserts.notEmptyText(zipCode, () -> new MessageException(Msg.required.args("郵便番号")));
    if (zipCode.length() != ZIP_CODE_SIZE || !zipCode.matches(NUMBER_REGULAR)) {
      throw new MessageException(Msg.invalidValue.args("郵便番号"));
    }
    return carMasterGokuApi
        .getAddress(zipCode)
        .doOnError(
            e -> {
              if (e instanceof GokuApiMessageException
                  && HttpStatus.NOT_FOUND.equals(
                      ((GokuApiMessageException) e).getRemoteHttpStatus())) {
                // goku から　データ取得できない場合
                throw new MessageException(Msg.invalidValue.args("郵便番号"));
              }
              throw new RuntimeException(e.getMessage(), e);
            })
        .map(AddressRes::getAddresses)
        .defaultIfEmpty(List.of(new Address()));
  }
}
