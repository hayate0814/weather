package com.jp.kinto.app.bff.contracts;

import static org.mockito.ArgumentMatchers.any;

import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.security.jwt.JwtTokenProvider;
import com.jp.kinto.app.bff.goku.CoreGokuApi;
import com.jp.kinto.app.bff.goku.response.GokuContractRegistResponse;
import com.jp.kinto.app.bff.goku.response.GokuContractRegistResponse.Application;
import com.jp.kinto.app.bff.goku.response.GokuContractRegistResponse.Contract;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import com.jp.kinto.app.bff.model.auth.AuthUser.Role;
import com.jp.kinto.app.bff.model.contractRegist.ContractRegistReq;
import com.jp.kinto.app.bff.model.contractRegist.ContractRegistRequest;
import com.jp.kinto.app.bff.model.contractRegist.ContractRegistRequest.ContractCar;
import com.jp.kinto.app.bff.repository.MemberContractRespository;
import com.jp.kinto.app.bff.repository.entity.MemberContract;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/registContract")
public class ContractRegistTest extends ApiTest {

  @MockBean private CoreGokuApi gokuApi;
  @Autowired private JwtTokenProvider jwtTokenProvider;
  @MockBean private MemberContractRespository memberContractRespository;

  @Test
  @DisplayName("正常系")
  void success_contracts() {
    GokuContractRegistResponse contractRegistResponse = new GokuContractRegistResponse();
    Application application = new Application();
    application.setId(1);
    application.setStatus("1");
    contractRegistResponse.setApplication(application);
    Contract contract = new Contract();
    contract.setId(1);
    contract.setKintoCoreMemberId("1");
    contract.setKintoCoreMemberId("1");
    contractRegistResponse.setContract(contract);
    Mockito.when(gokuApi.registContract(any(), any()))
        .thenReturn(Mono.just(contractRegistResponse));

    LocalDateTime now = LocalDateTime.now();
    MemberContract memberContract = new MemberContract();
    memberContract.setContractId("1");
    memberContract.setMemberId("1");
    memberContract.setCreateDatetime(now);
    memberContract.setCreateDatetime(now);
    Mockito.when(memberContractRespository.save(any())).thenReturn(Mono.just(memberContract));

    ContractRegistReq contractRegistReq = new ContractRegistReq();
    ContractRegistRequest contractRegistRequest = new ContractRegistRequest();
    contractRegistRequest.setMemberType("1");
    contractRegistRequest.setUserName("aaa");
    contractRegistRequest.setContractCar(new ContractCar());
    contractRegistReq.setContractRegistRequest(contractRegistRequest);
    webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path("/api/registContract").build())
        .body(Mono.just(contractRegistReq), ContractRegistReq.class)
        .header(
            "Authorization",
            "Bearer "
                + jwtTokenProvider.createToken(
                    AuthUser.builder().userId(1).role(Role.User).jpnKintoIdAuth("sss").build()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse);
  }
}
