package com.jp.kinto.app.bff.controller;

import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.goku.request.NoticeReq;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import com.jp.kinto.app.bff.model.contractIncludeInfo.ContractIncludeInfoResponse;
import com.jp.kinto.app.bff.model.contractRegist.ContractRegistReq;
import com.jp.kinto.app.bff.model.contractRegist.ContractRegistResponse;
import com.jp.kinto.app.bff.service.ContractIncludeInfoService;
import com.jp.kinto.app.bff.service.ContractRegistService;
import com.jp.kinto.app.bff.service.NoticeService;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping(Constant.API_URL_PREFIX)
public class ContractController {

  @Autowired private ContractRegistService contractRegistService;
  @Autowired private NoticeService noticeService;
  @Autowired private ContractIncludeInfoService contractIncludeInfoService;

  @PostMapping("/registContract")
  public Mono<ContractRegistResponse> registContract(
      Authentication authentication, @RequestBody ContractRegistReq request) {
    var authUser = (AuthUser) authentication.getCredentials();
    return contractRegistService.contractRegist(authUser, request);
  }

  @PostMapping("/notice")
  public Mono<Void> notice(@RequestBody NoticeReq request) {
    return Mono.delay(Duration.ofSeconds(5)).then(noticeService.pushNotice(request));
  }

  /**
   * 契約付帯情報取得
   *
   * @param makerCode メーカーコード
   * @param carModelCode 車種コード
   * @param gradeCode グレードコード
   * @return ContractIncludeInfoResponse
   */
  @GetMapping("/contractIncludeInfo")
  public Mono<ContractIncludeInfoResponse> getContractIncludeInfo(
      String makerCode, String carModelCode, String gradeCode) {
    return contractIncludeInfoService.getContractIncludeInfo(makerCode, carModelCode, gradeCode);
  }
}
