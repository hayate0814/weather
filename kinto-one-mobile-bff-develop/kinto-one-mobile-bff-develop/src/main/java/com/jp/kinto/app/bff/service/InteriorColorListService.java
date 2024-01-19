package com.jp.kinto.app.bff.service;

import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.core.exception.MessageException;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.response.InteriorColorRes;
import com.jp.kinto.app.bff.model.interiorColer.InteriorColorListResponse;
import com.jp.kinto.app.bff.utils.Asserts;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 内装色一覧取得取得 InteriorColorListService
 *
 * @author ZHANG-CHUANG
 */
@Service
@Slf4j
public class InteriorColorListService {

  @Autowired private CarMasterGokuApi gokuApi;

  private static final String FLOOR_MAT_SELECTION_STRING = "フロアマット有無･種類の選択";

  /**
   * 内装色一覧取得
   *
   * @return 内装色一覧情報
   */
  public Mono<List<InteriorColorListResponse>> getInteriorColorList(
      Integer contractMonths, String gradeId, String planType) {

    log.debug("InteriorColorList取得---START---");
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

    Mono<InteriorColorRes> gokuResponse =
        gokuApi.getInteriorColorList(contractMonths, gradeId, planType);

    Mono<List<InteriorColorListResponse>> monoInteriorColorListRes =
        gokuResponse
            .flatMapMany(res -> Flux.fromIterable(res.getData()))
            .map(
                data -> {
                  InteriorColorListResponse response = new InteriorColorListResponse();
                  response.setInteriorId(data.getId());
                  response.setInteriorName(data.getName());
                  response.setInteriorImageUrl(data.getInteriorImageFile());
                  response.setSeatColorName(data.getSeatColorName());
                  response.setMonthlyPrice(data.getMonthlyPrice());
                  response.setAdditionalApplicationCharge(data.getAdditionalApplicationCharge());
                  response.setAbbreviation(
                      FLOOR_MAT_SELECTION_STRING.equals(data.getFloorMat().getAbbreviation())
                          ? ""
                          : data.getFloorMat().getAbbreviation());
                  return response;
                })
            .collectList();

    log.debug("InteriorColorList取得---End---");

    return monoInteriorColorListRes;
  }
}
