package com.jp.kinto.app.bff.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.core.exception.MessageException;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.goku.request.NoticeReq;
import com.jp.kinto.app.bff.repository.LoginHistoryRepository;
import com.jp.kinto.app.bff.repository.NotificationSentJobRepository;
import com.jp.kinto.app.bff.repository.entity.NotificationSentJobEntity;
import com.jp.kinto.app.bff.utils.Asserts;
import com.jp.kinto.app.bff.utils.Casts;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * 契約ステータス更新Webhook NoticeService
 *
 * @author YE-FOFU
 */
@Service
@Slf4j
public class NoticeService {

  @Autowired private NotificationSentJobRepository notificationSentJobRepository;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private LoginHistoryRepository loginHistoryRepository;

  @Transactional
  public Mono<Void> pushNotice(NoticeReq request) {
    log.info("契約ステータス更新Webhook request-data: {}", Casts.toJson(objectMapper, request));

    Asserts.notNull(
        request.getOldContractId(), () -> new MessageException(Msg.required.args("旧契約ID")));
    Asserts.notEmptyText(
        request.getMemberId(), () -> new MessageException(Msg.required.args("メンバーID")));
    Asserts.notEmptyText(
        request.getTitle(), () -> new MessageException(Msg.required.args("通知タイトル")));
    Asserts.notEmptyText(request.getBody(), () -> new MessageException(Msg.required.args("通知本文")));

    /**
     * E01 お申し込みのご確認のお知らせ E02 お申込み手続き完了のお知らせ E04 【重要】：審査承認・契約お手続きのご案内 ES042 【重要】：審査承認・契約お手続きのご案内(廃止)
     * E05 審査完了のお知らせ E06 ご契約ありがとうございました E06-01 申込金のお支払いおよびご契約ありがとうございました E07 納車までの今後のお手続きに関するお知らせ
     * E08 納車準備中のお知らせ E09 お申し込み取消しのお知らせ
     */
    Set<String> mailTitleSet =
        new HashSet<>(
            Arrays.asList(
                "お申し込みのご確認のお知らせ",
                "お申込み手続き完了のお知らせ",
                "【重要】：審査承認・契約お手続きのご案内",
                "審査完了のお知らせ",
                "ご契約ありがとうございました",
                "申込金のお支払いおよびご契約ありがとうございました",
                "納車までの今後のお手続きに関するお知らせ",
                "納車準備中のお知らせ",
                "お申し込み取消しのお知らせ"));

    if (!mailTitleSet.contains(request.getTitle().trim())) {
      return Mono.empty();
    }

    return loginHistoryRepository
        .findOneByMemberId(request.getMemberId())
        .flatMap(
            loginHistory -> {
              LocalDateTime now = LocalDateTime.now();
              var notification = new NotificationSentJobEntity();
              notification.setTitle(request.getTitle().trim());
              notification.setContent(request.getBody());
              notification.setMemberId(request.getMemberId());
              notification.setNotificationKindSv(
                  Constant.NotificationKindSv.ContractStatusNtc.getSv());
              notification.setOldContractId(request.getOldContractId());
              notification.setSentFlag(Constant.Flag.NO.getSv());
              notification.setCreateDateTime(now);
              notification.setUpdateDateTime(now);
              return notificationSentJobRepository.save(notification).then();
            })
        .onErrorResume(
            e -> {
              return Mono.empty();
            });
  }
}
