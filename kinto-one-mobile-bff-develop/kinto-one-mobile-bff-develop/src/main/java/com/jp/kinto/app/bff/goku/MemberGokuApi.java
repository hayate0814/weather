package com.jp.kinto.app.bff.goku;

import static com.jp.kinto.app.bff.core.constant.Constant.SALES_CHANNEL;

import com.jp.kinto.app.bff.core.properties.GokuProperties;
import com.jp.kinto.app.bff.goku.client.GokuClient;
import com.jp.kinto.app.bff.goku.response.OuterPlateColorRes;
import java.util.function.Function;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/** メンバー関連API */
@Component
public class MemberGokuApi {
  private GokuClient gokuClient;
  private GokuProperties gokuProperties;

  public MemberGokuApi(Function<String, WebClient> webClientFun, GokuProperties gokuProperties) {
    this.gokuProperties = gokuProperties;
    this.gokuClient = new GokuClient(webClientFun.apply(gokuProperties.getUrl()));
  }

  public Mono<OuterPlateColorRes> getXXXXX(
      Integer contractMonths, String gradeId, String planType) {
    return gokuClient.call(
        OuterPlateColorRes.class,
        client ->
            client
                .get()
                .uri(
                    uriBuilder ->
                        uriBuilder
                            .path(
                                String.format(
                                    "/api/price/v1/%s/grades/%s/outer-plate-colors/%s?",
                                    SALES_CHANNEL, gradeId, contractMonths))
                            .queryParam("contractMonths", contractMonths)
                            .queryParam("id", gradeId)
                            .queryParam("planType", planType)
                            .build()));
  }
}
