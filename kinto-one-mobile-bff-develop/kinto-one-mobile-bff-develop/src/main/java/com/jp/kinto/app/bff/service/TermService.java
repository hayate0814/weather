package com.jp.kinto.app.bff.service;

import static com.jp.kinto.app.bff.core.constant.Constant.*;
import static com.jp.kinto.app.bff.core.constant.Constant.PlanSv.PlanA;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.core.constant.Constant.TConnect;
import com.jp.kinto.app.bff.core.exception.MessageException;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.core.properties.GokuProperties;
import com.jp.kinto.app.bff.model.rule.TermResponse;
import com.jp.kinto.app.bff.utils.Asserts;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class TermService {
  @Autowired private GokuProperties gokuProperties;

  public Mono<List<TermResponse>> getTerms(
      Boolean isLexus,
      String carModelEnglishName,
      String planType,
      String tconnect,
      Boolean isGr,
      Boolean isMorizou) {

    Asserts.assertTrue(
        Constant.PlanSv.isValidByCode(planType),
        () -> new MessageException(Msg.invalidValue.args("契約プラン")));
    Asserts.assertTrue(
        Constant.TConnect.isValidByCode(tconnect),
        () -> new MessageException(Msg.invalidValue.args("コネクティッド")));

    List<TermResponse> tmpList = getData();
    List<TermResponse> termResponseList = new ArrayList<>();
    // 申込手続き
    termResponseList.add(tmpList.get(TermSv.APPLICATION_PROCEDURE.getSv()));

    boolean isPlanTypeA = PlanA.getCode().equals(planType);
    // goku-webに合わせて、carEnglishNameからモリゾフラグを判定する
    boolean isMorizoModeFlag =
        CAR_NAME_INCLUDES_MS_KEY_LIST.stream().anyMatch(carModelEnglishName::contains);

    if (isPlanTypeA && isMorizoModeFlag) {
      // 取扱い販売店
      termResponseList.add(tmpList.get(TermSv.DEALER.getSv()));
      // メンテナンス
      termResponseList.add(tmpList.get(TermSv.MAINTENANCE.getSv()));
    }

    if (isGr) {
      // メンテナンス
      termResponseList.add(tmpList.get(TermSv.MAINTENANCE.getSv()));
    }

    if (isPlanTypeA) {
      // 支払方法
      termResponseList.add(tmpList.get(TermSv.PAYMENT_METHOD.getSv()));
    } else {
      // 申込金支払
      termResponseList.add(tmpList.get(TermSv.APPLICATION_FEE_PAYMENT.getSv()));
      // 月額使用料
      termResponseList.add(tmpList.get(TermSv.MONTHLY_FEE.getSv()));
      // 解約時における申込金扱い
      termResponseList.add(tmpList.get(TermSv.CANCELLATION_APPLICATION.getSv()));
    }

    // 納期
    termResponseList.add(tmpList.get(TermSv.DEADLINE.getSv()));
    // 車庫手配
    termResponseList.add(tmpList.get(TermSv.GARAGE_ARRANGEMENT.getSv()));
    // 禁止事項（２）
    termResponseList.add(tmpList.get(TermSv.PROHIBITED_MATTER_B.getSv()));

    if (isGr || (isMorizoModeFlag && isPlanTypeA)) {
      // 禁止事項（１）
      termResponseList.set(
          termResponseList.size() - 1, tmpList.get(TermSv.PROHIBITED_MATTER_A.getSv()));
    }

    // 個人情報
    termResponseList.add(tmpList.get(TermSv.PERSONAL_INFORMATION.getSv()));
    // 反社条項
    termResponseList.add(tmpList.get(TermSv.ANTISOCIAL_CLAUSE.getSv()));

    if (isPlanTypeA) {
      // 中途解約
      termResponseList.add(tmpList.get(TermSv.MIDTERM_CANCELLATION.getSv()));
    }

    if (TConnect.FULL_COVERAGE.getCode().equals(tconnect)) {
      // 特定の車種
      termResponseList.add(tmpList.get(TermSv.SPECIFIC_CAR.getSv()));
    }

    if (isLexus) {
      termResponseList
          .get(termResponseList.size() - 1)
          .setTerm(getLeaseTerm(isPlanTypeA ? TERM_3 : TERM_4));
    } else if (isGr) {
      termResponseList
          .get(termResponseList.size() - 1)
          .setTerm(getLeaseTerm(isPlanTypeA ? TERM_5 : TERM_6));
    } else if (isMorizoModeFlag) {
      termResponseList.get(termResponseList.size() - 1).setTerm(getLeaseTerm(TERM_5));
    } else {
      termResponseList
          .get(termResponseList.size() - 1)
          .setTerm(getLeaseTerm(isPlanTypeA ? TERM_1 : TERM_2));
    }
    // 規約同意
    termResponseList.add(tmpList.get(TermSv.TERM.getSv()));
    return Mono.just(termResponseList);
  }

  private List<TermResponse> getData() {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(
          new InputStreamReader(new ClassPathResource("json/termList.json").getInputStream()),
          new TypeReference<>() {});
    } catch (Exception e) {
      throw new RuntimeException("Failed to read term file(json/termList.json)", e);
    }
  }

  /**
   * getLeaseTerm
   *
   * @param leaseTermsContentsURL 対象URL
   */
  private String getLeaseTerm(String leaseTermsContentsURL) {

    try (val is = new ClassPathResource("html/leaseTerm.html").getInputStream()) {
      BufferedReader inputStreamReader =
          new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder();
      char[] c = new char[1024];
      for (int length = inputStreamReader.read(c); length != -1; ) {
        sb.append(c, 0, length);
        length = inputStreamReader.read(c);
      }

      InputStream in = new URL(gokuProperties.getUrl() + leaseTermsContentsURL).openStream();

      byte[] bytes = in.readAllBytes();
      var htmlString = new String(bytes, StandardCharsets.UTF_8);

      return sb.toString().replace("${leaseTermsContents}", htmlString);
    } catch (Exception e) {
      throw new RuntimeException("Failed to read term file(html/leaseTerm.html)", e);
    }
  }
}
