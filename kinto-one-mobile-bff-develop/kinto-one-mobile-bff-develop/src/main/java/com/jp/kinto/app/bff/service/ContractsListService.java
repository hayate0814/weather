package com.jp.kinto.app.bff.service;

import static com.jp.kinto.app.bff.core.message.Msg.format;

import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.core.exception.UnauthorizedException;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.CoreGokuApi;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import com.jp.kinto.app.bff.model.contracts.ContractsListResponse;
import com.jp.kinto.app.bff.utils.Asserts;
import com.jp.kinto.app.bff.utils.Casts;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * 契約一覧取得 contractsListService
 *
 * @author ZHANG-CHUANG
 */
@Service
@Slf4j
public class ContractsListService {

  @Autowired private CoreGokuApi gokuApi;
  @Autowired private CarMasterGokuApi gokuMstApi;

  // メンテナンス用
  private static final String NEW_CAR_INSPECTION_WORDING = "新車点検";
  private static final String LEGALLY_12_MONTHS_INSPECTION_WORDING = "法定12ヶ月点検";
  private static final String PRO_CARE_10_WORDING = "プロケア10";
  private static final String CAR_INSPECTION_MAINTENANCE_WORDING = "車検整備";
  private static final String CARE_1_MONTH_WORDING = "レクサス1ヶ月ケア";
  private static final String CARE_6_MONTHS_WORDING = "レクサス6ヶ月ケア";

  private static final String NEW_CAR_INSPECTION = "NEW_CAR_INSPECTION";
  private static final String LEGALLY_12_MONTHS_INSPECTION = "LEGALLY_12_MONTHS_INSPECTION";
  private static final String PRO_CARE_10 = "PRO_CARE_10";
  private static final String CAR_INSPECTION_MAINTENANCE = "CAR_INSPECTION_MAINTENANCE";
  private static final String CARE_1_MONTH = "CARE_1_MONTH";
  private static final String CARE_6_MONTHS = "CARE_6_MONTHS";
  // メンテナンス用

  private static final String SEND_MAIL = "SEND_MAIL"; // メール確認中
  private static final String REQUESTED = "REQUESTED"; // 申出
  private static final String ACCEPTED = "ACCEPTED"; // お申し込み受付
  private static final String IN_PROGRESS = "IN_PROGRESS"; // お申込内容確認中
  private static final String INCOMPLETE = "INCOMPLETE"; // 不備ご連絡中
  private static final String CREDIT_CHECK_NG = "CREDIT_CHECK_NG"; // 審査お見送り
  private static final String CREDIT_CHECK_OK_TFC = "CREDIT_CHECK_OK_TFC"; // 審査承認
  private static final String CONCLUSION = "CONCLUSION"; // 契約完了（車両手配中）
  private static final String SHIPMENT_PERIOD_FIXED = "SHIPMENT_PERIOD_FIXED"; // 工場出荷時期決定
  private static final String IN_PREPARATION_DELIVERY = "IN_PREPARATION_DELIVERY"; // 納車準備中
  private static final String UNDER_CONTRACT = "UNDER_CONTRACT"; // ご利用中
  private static final String EXPIRATION = "EXPIRATION"; // 契約満了
  private static final String CANCEL = "CANCEL"; // お申し込み取消し
  private static final String CANCELLED = "CANCELLED"; // 解約済

  private static final String NEW = "NEW"; // 新規
  private static final String CANCELLATION = "CANCELLATION"; // 中途解約

  // SEND_MAIL: メール確認中
  private static final String CONFIRM_MAIL =
      """
          <section><div>お申し込み手続きは弊社よりお送りするメールの到着確認をもって完了します。</div></section>
          """
          .trim();

  // ACCEPTED: 申込受付
  private static final String EXAMINATION_WAIT =
      """
          <section><div><span class="im-text-bold">お申し込みいただき、誠にありがとうございます。</span></div>
          <div>審査結果回答まで数日かかりますので今しばらくお待ちください。</div>
          <div><span class="im-text-gray">※状況により担当者よりご連絡させていただく場合がございます。</span></div></section>
          """
          .trim();

  // CREDIT_CHECK_NG: 審査お見送り
  private static final String DNT_PASS_CONTRACT =
      """
          <section><div>誠に恐縮ながら、お申し込みはお見送りとさせていただきます。</div>
          <div>ご理解のほど、何卒よろしくお願い申しあげます。</div></section>
          """
          .trim();

  // IN_PROGRESS: 申込内容確認中
  // INCOMPLETE: 不備ご連絡中
  private static final String EXAMINATION_DEFICIENCY =
      """
          <section><div>審査結果回答まで数日かかりますので今しばらくお待ちください。</div>
          <div><span class="im-text-gray">※状況により担当者よりご連絡させていただく場合がございます。</span></div></section>
          """
          .trim();

  // CREDIT_CHECK_OK_TFC: 審査承認
  private static final String EXAMINATION_APPROVAL_FLEX =
      """
          <section><div><span class="im-text-bold">お申し込みいただき、誠にありがとうございます。</span></div>
          <div>ご契約手続きの準備が完了しました。</div>
          <div>「車情報詳細」をご確認のうえ、下記より契約手続きをお願いいたします。</div></section>
          """
          .trim();

  // CREDIT_CHECK_OK_KINTO: 審査承認(KINTO)
  private static final String EXAMINATION_APPROVAL =
      """
          <section><div>ご契約手続きのため、担当販売店よりご連絡しますのでお待ちください。</div>
          <div>なお、審査結果の有効期間は１ヶ月となります。また、ご利用開始後も本画面が表示されます。</div>
          <div>あらかじめご了承ください。</div></section>
          """
          .trim();

  // CONCLUSION: 契約完了（車両手配中）
  private static final String VEHICLE_GRRANGEMENT =
      """
          <section><div>ご契約が完了いたしました。販売店を通じて車両手配中です。</div></section>
          """.trim();

  // SHIPMENT_PERIOD_FIXED: 工場出荷時期決定
  private static final String FACTORY_SHIPMENT =
      """
          <section><div>メーカーでの生産・工場出荷時期が決定いたしました。車両が販売店に到着後、納車日打合せのご連絡をさせていただきます。今しばらくお待ちください。</div></section>
          """
          .trim();

  // IN_PREPARATION_DELIVERY: 納車準備中
  private static final String SHIPPING_TIME =
      """
          <section><div>販売店にてお車をお受け取りになる際に必ず以下のボタンから確認を実施してください。</div>
          <div><span class="im-text-gray">※確認を実施しないとご利用情報などが確認いただけません。</span></div></section>
          """
          .trim();

  // （UNDER_CONTRACT: ご利用中はmessage非表示）
  // CANCEL: 申込取消し
  private static final String CANCEL_APPLICATION =
      """
          <section><div>お申し込みをお取り消しさせていただきました。なお、お手続きの再開はできませんので、新たにお申し込み手続きをお願いします。</div></section>
          """
          .trim();

  // （EXPIRATION: 契約満了はmessage非表示）
  // （CANCELLED: 解約済はmessage非表示）

  /**
   * 契約一覧取得
   *
   * @return 契約一覧情報
   */
  public Mono<List<ContractsListResponse>> getContractsList(AuthUser authUser) {
    // 会員かどうかチェック
    Asserts.notNull(authUser.getJpnKintoIdAuth(), () -> UnauthorizedException.MEMBER_UNAUTHORIZED);

    return gokuApi
        .getContractsList(authUser.getJpnKintoIdAuth())
        .flatMapIterable(list -> list)
        .filter(
            c ->
                NEW.equals(c.getApplication().getType())
                    || CANCELLATION.equals(c.getApplication().getType()))
        .flatMap(
            data -> {
              ContractsListResponse response = new ContractsListResponse();
              response.setContractId(data.getId());
              response.setContractCarName(data.getContractCar().getSelectedCarSettings().getName());
              response.setGradesName(
                  data.getContractCar().getSelectedCarSettings().getGrades()[0].getName());
              response.setCarModelColorImageFile(
                  data.getContractCar()
                      .getSelectedCarSettings()
                      .getGrades()[0]
                      .getOuterColors()[0]
                      .getCarModelColorImageFile());

              if (IN_PROGRESS.equals(data.getStatus())) {
                response.setApplicationStatus(data.getApplication().getStatus());
              } else {
                response.setApplicationStatus(data.getStatus());
              }

              response.setApplicationDate(data.getApplication().getRequestedAt());
              response.setContractStartedAt(data.getContractDate());
              response.setContractEndedAt(data.getExpirationDate());

              response.setContent(getContent(data.getApplication().getStatus(), data.getStatus()));

              response.setRegistNumber(
                  format(
                      "{}{}{}{}",
                      Casts.toString(data.getContractCar().getRegistrationNumberArea(), ""),
                      Casts.toString(data.getContractCar().getRegistrationNumberClass(), ""),
                      Casts.toString(data.getContractCar().getRegistrationNumberKana(), ""),
                      Casts.toString(data.getContractCar().getRegistrationNumberSpecified(), "")));

              YearMonth currentYearMonth = YearMonth.now();
              Arrays.stream(data.getContractCar().getCarMaintenances())
                  .filter(d -> !YearMonth.from(d.getExpectedDate()).isBefore(currentYearMonth))
                  .findFirst()
                  .ifPresent(
                      d -> {
                        response.setNextMaintenanceDate(
                            format(
                                "{}年{}月 {}",
                                d.getExpectedDate().getYear(),
                                d.getExpectedDate().getMonthValue(),
                                getCarMaintenancesWording(d.getType())));
                      });

              return gokuMstApi
                  .getDealers(data.getContractCar().getMaintenanceDealer().getCode(), true)
                  .map(
                      res -> {
                        response.setVendorName(
                            Constant.BrandSv.Toyota.getCode()
                                    .equals(
                                        data.getContractCar()
                                            .getSelectedCarSettings()
                                            .getMakerCode())
                                ? res.getDealerName()
                                : res.getStoreName());
                        return response;
                      });
            })
        .sort(Comparator.comparing(ContractsListResponse::getContractId).reversed())
        .collectList();
  }

  /**
   * getContent
   *
   * @param applicationStatus チェック対象
   * @param contractStatus チェック対象
   */
  private String getContent(String applicationStatus, String contractStatus) {

    if (IN_PROGRESS.equals(contractStatus)) {

      if (SEND_MAIL.equals(applicationStatus)) {
        return CONFIRM_MAIL;
      } else if (ACCEPTED.equals(applicationStatus)) {
        return EXAMINATION_WAIT;
      } else if (CREDIT_CHECK_NG.equals(applicationStatus)) {
        return DNT_PASS_CONTRACT;
      } else if (INCOMPLETE.equals(applicationStatus) || IN_PROGRESS.equals(applicationStatus)) {
        return EXAMINATION_DEFICIENCY;
      } else if (CREDIT_CHECK_OK_TFC.equals(applicationStatus)) {
        return EXAMINATION_APPROVAL_FLEX;
      } else if (CANCEL.equals(applicationStatus)) {
        return CANCEL_APPLICATION;
      } else {
        return "";
      }

    } else {
      if (CONCLUSION.equals(contractStatus)) {
        return VEHICLE_GRRANGEMENT;
      } else if (SHIPMENT_PERIOD_FIXED.equals(contractStatus)) {
        return FACTORY_SHIPMENT;
      } else if (IN_PREPARATION_DELIVERY.equals(contractStatus)) {
        return SHIPPING_TIME;
      } else {
        return "";
      }
    }
  }

  /**
   * getCarMaintenancesWording
   *
   * @param wordingType チェック対象
   */
  private String getCarMaintenancesWording(String wordingType) {

    if (NEW_CAR_INSPECTION.equals(wordingType)) {
      return NEW_CAR_INSPECTION_WORDING;
    } else if (LEGALLY_12_MONTHS_INSPECTION.equals(wordingType)) {
      return LEGALLY_12_MONTHS_INSPECTION_WORDING;
    } else if (PRO_CARE_10.equals(wordingType)) {
      return PRO_CARE_10_WORDING;
    } else if (CAR_INSPECTION_MAINTENANCE.equals(wordingType)) {
      return CAR_INSPECTION_MAINTENANCE_WORDING;
    } else if (CARE_1_MONTH.equals(wordingType)) {
      return CARE_1_MONTH_WORDING;
    } else if (CARE_6_MONTHS.equals(wordingType)) {
      return CARE_6_MONTHS_WORDING;
    } else {
      return "";
    }
  }
}
