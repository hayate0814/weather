package com.jp.kinto.app.bff.repository;

import com.jp.kinto.app.bff.repository.entity.UserDevice;
import java.time.LocalDateTime;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserDeviceRepository extends ReactiveCrudRepository<UserDevice, Integer> {
  @Query("SELECT * FROM user_device WHERE member_id = :memberId")
  public Flux<UserDevice> findDeviceByMemberId(String memberId);

  @Query("SELECT * FROM user_device WHERE member_id = :memberId LIMIT 1")
  public Mono<UserDevice> findOneDeviceByMemberId(String memberId);

  @Modifying
  @Query(
      "UPDATE user_device SET notice_token=:token, update_datetime=:updateDateTime WHERE"
          + " user_id=:id")
  public Mono<Integer> updateNoticeToken(Integer id, String token, LocalDateTime updateDateTime);

  @Modifying
  @Query(
      "UPDATE user_device SET member_id=:memberId, update_datetime=:updateDateTime WHERE"
          + " user_id=:id")
  public Mono<Integer> updateMemberId(Integer id, String memberId, LocalDateTime updateDateTime);
}
