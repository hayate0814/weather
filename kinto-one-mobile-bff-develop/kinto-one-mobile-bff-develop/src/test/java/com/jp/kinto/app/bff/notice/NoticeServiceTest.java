package com.jp.kinto.app.bff.notice;

import static org.mockito.ArgumentMatchers.any;

import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.properties.WebHookProperties;
import com.jp.kinto.app.bff.goku.request.NoticeReq;
import com.jp.kinto.app.bff.repository.MemberContractRespository;
import com.jp.kinto.app.bff.repository.NotificationSentJobRepository;
import com.jp.kinto.app.bff.repository.UserDeviceRepository;
import com.jp.kinto.app.bff.repository.entity.MemberContract;
import com.jp.kinto.app.bff.repository.entity.NotificationSentJobEntity;
import com.jp.kinto.app.bff.repository.entity.UserDevice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/notice")
public class NoticeServiceTest extends ApiTest {

  @Autowired private WebHookProperties webHookProperties;
  @MockBean private UserDeviceRepository userDeviceRepository;
  @MockBean private NotificationSentJobRepository notificationSentJobRepository;
  @MockBean private MemberContractRespository memberContractRespository;

  @Test
  @DisplayName("入力チェック_契約ID")
  void fail_01() throws InterruptedException {

    NoticeReq noticeReq = new NoticeReq();

    webClient
        .post()
        .uri("/api/notice")
        .header("mobile-api-access-key", webHookProperties.getMobileApiAccessKey())
        .body(Mono.just(noticeReq), NoticeReq.class)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("""
                {"code":1,"message":"旧契約IDは必須項目です"}
                """);
  }

  @Test
  @DisplayName("入力チェック_メンバーID")
  void fail_02() {

    NoticeReq noticeReq = new NoticeReq();
    noticeReq.setOldContractId("ENT-00001212");

    webClient
        .post()
        .uri("/api/notice")
        .header("mobile-api-access-key", webHookProperties.getMobileApiAccessKey())
        .body(Mono.just(noticeReq), NoticeReq.class)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("""
                    {"code":1,"message":"メンバーIDは必須項目です"}
                    """);
  }

  @Test
  @DisplayName("入力チェック_通知タイトル")
  void fail_05() {

    NoticeReq noticeReq = new NoticeReq();
    noticeReq.setOldContractId("ENT-00001212");
    noticeReq.setMemberId("MEM-0120002951");

    webClient
        .post()
        .uri("/api/notice")
        .header("mobile-api-access-key", webHookProperties.getMobileApiAccessKey())
        .body(Mono.just(noticeReq), NoticeReq.class)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("""
                    {"code":1,"message":"通知タイトルは必須項目です"}
                    """);
  }

  @Test
  @DisplayName("入力チェック_通知本文")
  void fail_06() {

    NoticeReq noticeReq = new NoticeReq();
    noticeReq.setOldContractId("ENT-00001212");
    noticeReq.setMemberId("MEM-0120002951");
    noticeReq.setTitle("お申込のご確認のお知らせ");

    webClient
        .post()
        .uri("/api/notice")
        .header("mobile-api-access-key", webHookProperties.getMobileApiAccessKey())
        .body(Mono.just(noticeReq), NoticeReq.class)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isBadRequest()
        .expectBody()
        .json("""
                    {"code":1,"message":"通知本文は必須項目です"}
                    """);
  }

  @Test
  @DisplayName("正常_Webhook")
  void success_01() {

    NoticeReq noticeReq = new NoticeReq();
    noticeReq.setOldContractId("ENT-00001212");
    noticeReq.setMemberId("MEM-0120002951");
    noticeReq.setTitle("お申込のご確認のお知らせ");
    noticeReq.setBody("通知本文");

    Mockito.when(userDeviceRepository.findOneDeviceByMemberId(any(String.class)))
        .thenReturn(Mono.just(new UserDevice().setMemberId("MEM-0120002951")));
    Mockito.when(notificationSentJobRepository.save(any()))
        .thenReturn(Mono.just(new NotificationSentJobEntity()));
    Mockito.when(memberContractRespository.findMemberContractById(any(), any()))
        .thenReturn(Mono.just(new MemberContract()));

    webClient
        .post()
        .uri("/api/notice")
        .header("mobile-api-access-key", webHookProperties.getMobileApiAccessKey())
        .body(Mono.just(noticeReq), NoticeReq.class)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }
}
