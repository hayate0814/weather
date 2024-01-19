package com.jp.kinto.app.bff.contracts;

import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.security.jwt.JwtTokenProvider;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.CoreGokuApi;
import com.jp.kinto.app.bff.goku.response.ContractDealerRes;
import com.jp.kinto.app.bff.goku.response.ContractsRes;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import com.jp.kinto.app.bff.model.auth.AuthUser.Role;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/contractsList")
public class ContractsListTest extends ApiTest {

  @MockBean private CoreGokuApi gokuApi;
  @MockBean private CarMasterGokuApi carMasterGokuApi;
  @Autowired private JwtTokenProvider jwtTokenProvider;

  @Test
  @DisplayName("正常系")
  void success_contractsList() {
    List<ContractsRes> contractsResList =
        parseJsonFile(
            INPUT_ROOT + "/contractList/success_contract_list_data.json", new TypeReference<>() {});

    Mockito.when(gokuApi.getContractsList(any(String.class)))
        .thenReturn(Mono.just(contractsResList));

    ContractDealerRes contractDealerRes = new ContractDealerRes();
    Mockito.when(carMasterGokuApi.getDealers(any(), any()))
        .thenReturn(Mono.just(contractDealerRes));
    webClient
        .get()
        .uri("/api/contractsList")
        .header(
            "Authorization",
            "Bearer "
                + jwtTokenProvider.createToken(
                    AuthUser.builder().userId(1).role(Role.User).jpnKintoIdAuth("sss").build()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("ログイン状態が無効")
  void failure_01() {
    List<ContractsRes> contractsResList =
        parseJsonFile(
            INPUT_ROOT + "/contractList/success_contract_list_data.json", new TypeReference<>() {});

    Mockito.when(gokuApi.getContractsList(any(String.class)))
        .thenReturn(Mono.just(contractsResList));
    webClient
        .get()
        .uri("/api/contractsList")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isUnauthorized()
        .expectBody()
        .json("{\"code\":2,\"message\":\"ログイン状態が無効です。\"}");
  }

  @Test
  @DisplayName("データ取得失敗時")
  void failure_02() {
    Mockito.when(gokuApi.getContractsList(any(String.class)))
        .thenThrow(new RuntimeException("test"));
    webClient
        .get()
        .uri("/api/contractsList")
        .header(
            "Authorization",
            "Bearer "
                + jwtTokenProvider.createToken(
                    AuthUser.builder().userId(1).role(Role.User).jpnKintoIdAuth("sss").build()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError();
  }
}
