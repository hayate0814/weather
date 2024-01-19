package com.jp.kinto.app.bff.repository;

import com.jp.kinto.app.bff.repository.entity.LoginHistory;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface LoginHistoryRepository extends ReactiveCrudRepository<LoginHistory, Integer> {

  @Query("SELECT * FROM login_history WHERE member_id = :memberId LIMIT 1")
  public Mono<LoginHistory> findOneByMemberId(String memberId);

  @Query("SELECT * FROM login_history WHERE user_id = :userId AND member_id = :memberId LIMIT 1")
  public Mono<LoginHistory> findOneByUserAndMemberId(Integer userId, String memberId);
}
