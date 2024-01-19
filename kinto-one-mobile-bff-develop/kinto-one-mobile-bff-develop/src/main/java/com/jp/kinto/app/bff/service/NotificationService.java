package com.jp.kinto.app.bff.service;

import com.jp.kinto.app.bff.core.exception.BadRequestException;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import com.jp.kinto.app.bff.model.notification.Notification;
import com.jp.kinto.app.bff.model.notification.NotificationDetail;
import com.jp.kinto.app.bff.repository.MemberNotificationHistoryRepository;
import com.jp.kinto.app.bff.repository.UserDeviceRepository;
import com.jp.kinto.app.bff.repository.entity.UserDevice;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class NotificationService {

  @Autowired private UserDeviceRepository userDeviceRepository;

  @Autowired private MemberNotificationHistoryRepository memberNotificationHistoryRepository;

  public Flux<Notification> getNotificationList(AuthUser authUserParam) {
    return Flux.just(authUserParam.getUserId())
        .flatMap(
            userId -> {
              UserDevice userDevice = new UserDevice();
              // ユーザデバイス検索(メンバ情報取得)
              if (Optional.ofNullable(authUserParam.getMemberId()).isEmpty()) {
                return userDeviceRepository.findById(userId);
              } else {
                return Mono.just(userDevice.setMemberId(authUserParam.getMemberId()));
              }
            })
        .flatMap(
            userDevice -> {
              if (Optional.ofNullable(userDevice.getMemberId()).isEmpty()) {
                // メンバIDが存在しない場合
                throw new BadRequestException(
                    BadRequestException.ErrorCode.LogicMessage, Msg.NotLoginUser);
              } else {
                // メンバIDが存在する場合
                return memberNotificationHistoryRepository
                    .findByMemberId(userDevice.getMemberId())
                    .map(
                        e ->
                            Notification.builder()
                                .notificationId(e.getNotificationId())
                                .title(e.getTitle())
                                .createDateTime(
                                    e.getCreateDatetime()
                                        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                                .readFlag(e.getReadFlag())
                                .build());
              }
            });
  }

  public Mono<NotificationDetail> getNotificationDetail(
      AuthUser authUserParam, Integer notificationId) {

    return Mono.just(authUserParam.getUserId())
        .flatMap(
            userId -> {
              UserDevice userDevice = new UserDevice();
              // ユーザデバイス検索(メンバ情報取得)
              if (Optional.ofNullable(authUserParam.getMemberId()).isEmpty()) {
                return userDeviceRepository.findById(userId);
              } else {
                return Mono.just(userDevice.setMemberId(authUserParam.getMemberId()));
              }
            })
        .flatMap(
            userDevice -> {
              if (Optional.ofNullable(userDevice.getMemberId()).isEmpty()) {
                // メンバIDが存在しない場合
                throw new BadRequestException(
                    BadRequestException.ErrorCode.LogicMessage, Msg.NotLoginUser);
              } else {
                // メンバIDが存在する場合
                return memberNotificationHistoryRepository
                    .findByIdAndMemberId(notificationId, userDevice.getMemberId())
                    .switchIfEmpty(
                        Mono.error(
                            new BadRequestException(
                                BadRequestException.ErrorCode.LogicMessage,
                                Msg.NotificationNotExist)));
              }
            })
        .flatMap(
            notificationEntity -> {
              // read_flag=1:既読にする
              notificationEntity.setReadFlag(1);
              return memberNotificationHistoryRepository
                  .save(notificationEntity)
                  .map(
                      saveEntity ->
                          NotificationDetail.builder()
                              .title(notificationEntity.getTitle())
                              .createDateTime(
                                  notificationEntity
                                      .getCreateDatetime()
                                      .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                              .content(notificationEntity.getContent())
                              .build());
            });
  }

  public Mono<Integer> getNumberUnreadNotification(AuthUser authUserParam) {

    return Mono.just(authUserParam.getUserId())
        .flatMap(
            userId -> {
              UserDevice userDevice = new UserDevice();
              // ユーザデバイス検索(メンバ情報取得)
              if (Optional.ofNullable(authUserParam.getMemberId()).isEmpty()) {
                return userDeviceRepository.findById(userId);
              } else {
                return Mono.just(userDevice.setMemberId(authUserParam.getMemberId()));
              }
            })
        .flatMap(
            userDevice -> {
              if (Optional.ofNullable(userDevice.getMemberId()).isEmpty()) {
                // メンバIDが存在しない場合
                return Mono.just(0);
              } else {
                // メンバIDが存在する場合
                return memberNotificationHistoryRepository.getNumberUnreadNotification(
                    userDevice.getMemberId());
              }
            });
  }
}
