package com.jp.kinto.app.bff.address;

import static org.mockito.ArgumentMatchers.any;

import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.exception.BadRequestException;
import com.jp.kinto.app.bff.core.exception.BadRequestException.ErrorCode;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.response.AddressRes;
import com.jp.kinto.app.bff.model.jsonData.Address;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/addressSearch")
public class AddressSearchTest extends ApiTest {

  @MockBean private CarMasterGokuApi carMasterGokuApi;

  @Test
  @DisplayName("addressSearch_success")
  void addressSearch_success() {
    AddressRes addressRes = new AddressRes();
    addressRes.setAddresses(
        Collections.singletonList(
            Address.builder().zipCode("3320034").prefecture("埼玉県").city("川口市").town("並木").build()));
    Mockito.when(carMasterGokuApi.getAddress(any())).thenReturn(Mono.just(addressRes));
    webClient
        .get()
        .uri("/api/addressSearch?zipCode=3320034")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .json(
            "[{\"zipCode\":\"3320034\",\"prefecture\":\"埼玉県\",\"city\":\"川口市\",\"town\":\"並木\"}]");
  }

  @Test
  @DisplayName("addressSearch_failure")
  void addressSearch_failure() {
    Mockito.when(carMasterGokuApi.getAddress(any()))
        .thenThrow(new BadRequestException(ErrorCode.InputError, "データを取得出来ませんでした。"));
    webClient
        .get()
        .uri("/api/addressSearch?zipCode=3329999")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("{\"code\":1,\"message\":\"データを取得出来ませんでした。\"}");
  }

  @Test
  @DisplayName("addressSearch_unauthorized")
  void addressSearch_unauthorized() {
    Mockito.when(carMasterGokuApi.getAddress(any())).thenReturn(Mono.just(new AddressRes()));
    webClient
        .get()
        .uri("/api/addressSearch?zipCode=3320034")
        .accept(MediaType.APPLICATION_JSON)
        .header("Authorization", "")
        .exchange()
        .expectStatus()
        .isUnauthorized();
  }

  @Test
  @DisplayName("addressSearch_versionError")
  void addressSearch_versionError() {
    Mockito.when(carMasterGokuApi.getAddress(any())).thenReturn(Mono.just(new AddressRes()));
    webClient
        .get()
        .uri("/api/addressSearch?zipCode=3320034")
        .accept(MediaType.APPLICATION_JSON)
        .header("Version", "")
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("{\"code\":1,\"message\":\"Versionヘッダーの値が正しくありません\"}");
  }

  @Test
  @DisplayName("addressSearch_zipCodeSizeError")
  void addressSearch_zipCodeSizeError() {
    Mockito.when(carMasterGokuApi.getAddress(any())).thenReturn(Mono.just(new AddressRes()));
    webClient
        .get()
        .uri("/api/addressSearch?zipCode=33200034")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("{\"code\":1,\"message\":\"郵便番号の値が正しくありません\"}");
  }

  @Test
  @DisplayName("addressSearch_zipCodeError")
  void addressSearch_zipCodeError() {
    Mockito.when(carMasterGokuApi.getAddress(any())).thenReturn(Mono.just(new AddressRes()));
    webClient
        .get()
        .uri("/api/addressSearch?zipCode=332003A")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("{\"code\":1,\"message\":\"郵便番号の値が正しくありません\"}");
  }
}
