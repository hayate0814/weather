package com.jp.kinto.app.bff.service;

import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.core.exception.MessageException;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.response.OuterPlateColorRes;
import com.jp.kinto.app.bff.model.OuterPlateColorListResponse;
import com.jp.kinto.app.bff.utils.Asserts;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 外板色一覧取得取得 OuterPlateColorListService
 *
 * @author YE-FOFU
 */
@Service
@Slf4j
public class OuterPlateColorListService {

  @Autowired private CarMasterGokuApi gokuApi;

  /**
   * 外板色一覧取得
   *
   * @return 外板色一覧情報
   */
  public Mono<List<OuterPlateColorListResponse>> getOuterPlateColorList(
      Integer contractMonths, String gradeId, String planType) {

    log.debug("OuterPlateColorList取得---START---");
    // 入力チェック
    Asserts.notNull(contractMonths, () -> new MessageException(Msg.required.args("契約月数")));
    Asserts.notEmptyText(gradeId, () -> new MessageException(Msg.required.args("グレードID")));
    Asserts.notEmptyText(planType, () -> new MessageException(Msg.required.args("契約プラン")));
    Asserts.assertTrue(
        Constant.PlanSv.isValidByCode(planType),
        () -> new MessageException(Msg.invalidValue.args("契約プラン")));
    Asserts.isValidSystemValue(
        Constant.ContractMonthSv.class,
        contractMonths,
        () -> new MessageException(Msg.invalidValue.args("契約月数")));

    Mono<OuterPlateColorRes> gokuResponse =
        gokuApi.getOuterPlateColorList(contractMonths, gradeId, planType);

    Mono<List<OuterPlateColorListResponse>> monoOuterPlateColorListRes =
        gokuResponse
            .flatMapMany(res -> Flux.fromIterable(res.getData()))
            .map(
                data -> {
                  OuterPlateColorListResponse response = new OuterPlateColorListResponse();
                  response.setOuterPlateColorId(data.getId());
                  response.setCarModelImageUrl(data.getCarModelColorImageFile());
                  response.setRecommendedOuterPlateColorIconCode(data.getRecommendationComment());
                  response.setOuterPlateColorPaletteCode(data.getPaletteCode1());
                  response.setOuterPlateColorPaletteCode2(data.getPaletteCode2());
                  response.setAdditionalApplicationCharge(data.getAdditionalApplicationCharge());
                  response.setMonthlyPrice(data.getMonthlyPrice());
                  return response;
                })
            .collectList();

    log.debug("OuterPlateColorList取得---End---");

    return monoOuterPlateColorListRes;
  }
}
