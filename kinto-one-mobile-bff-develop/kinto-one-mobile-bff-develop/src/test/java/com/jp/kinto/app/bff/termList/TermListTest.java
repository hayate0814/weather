package com.jp.kinto.app.bff.termList;

import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.properties.GokuProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

@SpringBootTest
@DisplayName("/api/terms")
public class TermListTest extends ApiTest {

  @MockBean private GokuProperties gokuProperties;

  @Test
  @DisplayName("termList_success_planA")
  void termList_success_planA() {
    Mockito.when(gokuProperties.getUrl()).thenReturn("https://kinto-jp.com");
    webClient
        .get()
        .uri("/api/terms?carModelEnglishName=u-prius&planType=PLAN_A&isLexus=true&tconnect=NONE")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("termList_success_planB")
  void termList_success_planB() {
    Mockito.when(gokuProperties.getUrl()).thenReturn("https://kinto-jp.com");
    webClient
        .get()
        .uri("/api/terms?carModelEnglishName=u-prius&planType=PLAN_B&isLexus=true&tconnect=NONE")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("termList_planType_error")
  void termList_planType_error() {
    webClient
        .get()
        .uri("/api/terms?carModelEnglishName=u-prius&planType=PLAN_C&isLexus=true&tconnect=NONE")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("""
            {"code":1,"message":"契約プランの値が正しくありません"}
            """);
  }

  @Test
  @DisplayName("termList_tconnect_error")
  void termList_tconnect_error() {
    webClient
        .get()
        .uri("/api/terms?carModelEnglishName=u-prius&planType=PLAN_A&isLexus=true&tconnect=NONEA")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("""
            {"code":1,"message":"コネクティッドの値が正しくありません"}
            """);
  }
}
