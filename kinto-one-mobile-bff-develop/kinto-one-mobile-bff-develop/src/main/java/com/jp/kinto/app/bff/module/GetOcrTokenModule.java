package com.jp.kinto.app.bff.module;

import com.jp.kinto.app.bff.core.exception.OCRBadRequestException;
import com.jp.kinto.app.bff.goku.CloudOcrApi;
import com.jp.kinto.app.bff.model.ocr.OcrResponse;
import com.jp.kinto.app.bff.repository.SettingMasterRepository;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Component
public class GetOcrTokenModule {
  @Autowired SettingMasterRepository settingMasterRepository;
  @Autowired CloudOcrApi cloudOcrApi;

  @Transactional
  public Mono<OcrResponse> getOcrToken(String smKey) {
    return settingMasterRepository
        .getSettingMasterKeyByKeyForUpdate(smKey)
        .onErrorMap(OCRBadRequestException::new)
        .flatMap(
            item ->
                settingMasterRepository
                    .getSettingMasterValueByKey(smKey)
                    .onErrorMap(OCRBadRequestException::new)
                    .flatMap(
                        res -> {
                          if (res.getSmValue() != null) {
                            return Mono.just(
                                OcrResponse.builder().ocrToken(res.getSmValue()).build());
                          } else {
                            return cloudOcrApi
                                .getAccess()
                                .flatMap(
                                    ocrRes ->
                                        settingMasterRepository
                                            .updateSettingMasterValueByKey(
                                                ocrRes.getAccessToken(), LocalDateTime.now(), smKey)
                                            .onErrorMap(OCRBadRequestException::new)
                                            .thenReturn(
                                                OcrResponse.builder()
                                                    .ocrToken(ocrRes.getAccessToken())
                                                    .build()));
                          }
                        }));
  }
}
