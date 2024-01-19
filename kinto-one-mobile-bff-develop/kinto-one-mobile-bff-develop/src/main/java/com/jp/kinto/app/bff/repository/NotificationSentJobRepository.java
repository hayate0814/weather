package com.jp.kinto.app.bff.repository;

import com.jp.kinto.app.bff.repository.entity.NotificationSentJobEntity;
import java.util.List;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationSentJobRepository
    extends ReactiveCrudRepository<NotificationSentJobEntity, Integer> {
  @Query("SELECT * FROM notification_sent_job WHERE sent_flag = 0 limit :limit")
  List<NotificationSentJobEntity> unSendNotifications(@Param("limit") int limit);
}
