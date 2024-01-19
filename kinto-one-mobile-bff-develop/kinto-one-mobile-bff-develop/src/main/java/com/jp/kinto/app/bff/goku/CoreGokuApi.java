package com.jp.kinto.app.bff.goku;

import static com.jp.kinto.app.bff.core.constant.Constant.GOKU_API_VERSION;
import static com.jp.kinto.app.bff.core.constant.Constant.JPN_KINTO_ID_AUTH;

import com.jp.kinto.app.bff.core.properties.GokuProperties;
import com.jp.kinto.app.bff.goku.client.GokuClient;
import com.jp.kinto.app.bff.goku.response.ContractsRes;
import com.jp.kinto.app.bff.goku.response.GokuContractRegistResponse;
import com.jp.kinto.app.bff.model.contractRegist.ContractRegistReq;
import com.jp.kinto.app.bff.model.contractRegist.ContractRegistRequest;
import java.util.List;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/** 申し込み、Mykintoなどで利用するAPI */
@Component
@Slf4j
public class CoreGokuApi {
  private GokuClient gokuClient;
  private GokuProperties gokuProperties;

  public CoreGokuApi(Function<String, WebClient> webClientFun, GokuProperties gokuProperties) {
    this.gokuProperties = gokuProperties;
    this.gokuClient = new GokuClient(webClientFun.apply(gokuProperties.getUrl()));
  }

  public Mono<GokuContractRegistResponse> registContract(
      ContractRegistReq request, String jpnKintoIdAuth) {
    if (!StringUtils.hasText(jpnKintoIdAuth)) {
      // 契約申し込み(ユーザー新規作成含む)
      return gokuClient.call(
          GokuContractRegistResponse.class,
          client ->
              client
                  .post()
                  .uri(String.format("/api/core/%s/new-applications/new-member", GOKU_API_VERSION))
                  .body(Mono.just(request), ContractRegistReq.class));
    } else {
      // 契約申し込み(ユーザー登録済み)
      ContractRegistRequest contractRegist = new ContractRegistRequest();
      BeanUtils.copyProperties(request.getContractRegistRequest(), contractRegist);
      return gokuClient.call(
          GokuContractRegistResponse.class,
          client ->
              client
                  .post()
                  .uri(String.format("/api/core/%s/new-applications", GOKU_API_VERSION))
                  .header("Cookie", JPN_KINTO_ID_AUTH + "=" + jpnKintoIdAuth)
                  .body(Mono.just(contractRegist), ContractRegistRequest.class));
    }
  }

  public Mono<List<ContractsRes>> getContractsList(String jpnKintoIdAuth) {
    return gokuClient.call(
        new ParameterizedTypeReference<>() {},
        client ->
            client
                .get()
                .uri(String.format("/api/core/%s/contracts", GOKU_API_VERSION))
                .header("Cookie", JPN_KINTO_ID_AUTH + "=" + jpnKintoIdAuth));
  }
}
