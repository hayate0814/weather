package com.jp.kinto.app.bff.repository;

import com.jp.kinto.app.bff.repository.entity.NotificationEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MemberNotificationHistoryRepository
    extends ReactiveCrudRepository<NotificationEntity, String> {
  @Query("SELECT * FROM member_notification_history WHERE member_id = :memberId")
  Flux<NotificationEntity> findByMemberId(String memberId);

  @Query(
      "SELECT * FROM member_notification_history WHERE notification_id = :id AND member_id ="
          + " :memberId")
  Mono<NotificationEntity> findByIdAndMemberId(Integer id, String memberId);

  @Query(
      "SELECT count(*) FROM member_notification_history WHERE member_id = :memberId AND read_flag ="
          + " 0")
  Mono<Integer> getNumberUnreadNotification(String memberId);
}
