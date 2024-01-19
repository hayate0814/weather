package com.jp.kinto.app.bff.service;

import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.core.exception.OCRBadRequestException;
import com.jp.kinto.app.bff.model.ocr.OcrResponse;
import com.jp.kinto.app.bff.module.GetOcrTokenModule;
import com.jp.kinto.app.bff.repository.SettingMasterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class OcrService {
  @Autowired SettingMasterRepository settingMasterRepository;
  @Autowired GetOcrTokenModule getOcrTokenModule;

  public Mono<OcrResponse> getOcrToken() {
    String smKey = Constant.SettingMasterKey.OCR_TOKEN.toString();
    return settingMasterRepository
        .getSettingMasterValueByKey(smKey)
        .onErrorMap(OCRBadRequestException::new)
        .flatMap(
            res -> {
              if (res.getSmValue() != null) {
                return Mono.just(OcrResponse.builder().ocrToken(res.getSmValue()).build());
              } else {
                return getOcrTokenModule.getOcrToken(smKey);
              }
            });
  }
}
