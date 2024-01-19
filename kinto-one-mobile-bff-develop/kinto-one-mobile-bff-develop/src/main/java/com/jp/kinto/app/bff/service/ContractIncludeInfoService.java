package com.jp.kinto.app.bff.service;

import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.core.constant.Constant.BrandSv;
import com.jp.kinto.app.bff.core.exception.MessageException;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.model.contractIncludeInfo.ContractIncludeInfoResponse;
import com.jp.kinto.app.bff.model.contractIncludeInfo.ContractIncludeInfoResponse.MonthlyIncludedContent;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ContractIncludeInfoService {

  @Autowired private CarMasterGokuApi carMasterGokuApi;

  private static final List<String> TOYOTA_INSURANCE =
      Arrays.asList(
          "・自賠責保険",
          "・任意保険\n   対人・対物賠償責任保険：無制限\n   人身傷害：上限１名につき5,000万円",
          "・車両保険：上限規定損害金【自己負担額：1事故5万円】  等");
  private static final List<String> LEXUS_INSURANCE =
      Arrays.asList(
          "・自賠責保険",
          "・任意保険\n   対人・対物賠償責任保険：無制限\n   人身傷害：上限１名につき一億円",
          "・車両保険：上限規定損害金【自己負担額：1事故5万円】  等");
  private static final List<String> TAX_FEE = Arrays.asList("・自動車税", "・重量税", "・登録手続き費用等");
  private static final List<String> TOYOTA_MAINTENANCE =
      Arrays.asList(
          "・法定点検",
          "・定期点検",
          "・故障修理",
          "・代車",
          "・ロードサービス",
          "・油脂類の交換及び補充",
          "・所定の消耗品の交換",
          "・車検（契約期間3年の場合を除く）");
  private static final List<String> LEXUS_MAINTENANCE = List.of("・レクサスケアメンテナンスプログラムが適用");
  private static final List<String> OTHER_MAINTENANCE =
      Arrays.asList(
          "・法定点検", "・故障修理", "・代車", "・ロードサービス", "・油脂類の交換及び補充", "・所定の消耗品の交換", "・車検（契約期間3年の場合を除く）");
  private static final List<String> T_CONNECTED = Arrays.asList("・T-Connect基本サービス");

  public Mono<ContractIncludeInfoResponse> getContractIncludeInfo(
      String makerCode, String carModelCode, String gradeCode) {
    if (!BrandSv.isValidByCode(makerCode)) {
      throw new MessageException(Msg.invalidValue.args("メーカーコード"));
    }

    return carMasterGokuApi
        .getCharacteristics(carModelCode, gradeCode)
        .map(
            e -> {
              if (BrandSv.Lexus.getCode().equals(makerCode)) {
                return ContractIncludeInfoResponse.builder()
                    .monthlyIncludedContent(
                        MonthlyIncludedContent.builder()
                            .insurance(LEXUS_INSURANCE)
                            .taxFee(TAX_FEE)
                            .maintenance(LEXUS_MAINTENANCE)
                            .build())
                    .carCharacteristics(e.get(0))
                    .build();
              } else {
                if (e.get(0).getIsUnlimited()) {
                  return ContractIncludeInfoResponse.builder()
                      .monthlyIncludedContent(
                          MonthlyIncludedContent.builder()
                              .insurance(TOYOTA_INSURANCE)
                              .taxFee(TAX_FEE)
                              .maintenance(OTHER_MAINTENANCE)
                              .build())
                      .carCharacteristics(e.get(0))
                      .build();
                } else {
                  return ContractIncludeInfoResponse.builder()
                      .monthlyIncludedContent(
                          MonthlyIncludedContent.builder()
                              .insurance(TOYOTA_INSURANCE)
                              .taxFee(TAX_FEE)
                              .maintenance(TOYOTA_MAINTENANCE)
                              .build())
                      .carCharacteristics(e.get(0))
                      .build();
                }
              }
            })
        .map(
            e -> {
              // add connected
              e.getMonthlyIncludedContent()
                  .setConnected(
                      Constant.TConnect.FULL_COVERAGE
                              .getCode()
                              .equals(e.getCarCharacteristics().getTconnect())
                          ? T_CONNECTED
                          : null);
              return e;
            });
  }
}
