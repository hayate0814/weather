package com.jp.kinto.app.bff.repository;

import com.jp.kinto.app.bff.repository.entity.SettingMasterEntity;
import java.time.LocalDateTime;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SettingMasterRepository extends ReactiveCrudRepository<SettingMasterEntity, Void> {
  @Query("SELECT sm_value FROM setting_master WHERE sm_key =:smKey")
  public Mono<SettingMasterEntity> getSettingMasterValueByKey(String smKey);

  @Query("SELECT sm_key FROM setting_master WHERE sm_key=:smKey FOR UPDATE")
  public Mono<SettingMasterEntity> getSettingMasterKeyByKeyForUpdate(String smKey);

  @Modifying
  @Query(
      "UPDATE setting_master SET sm_value=:smValue, update_datetime=:updateDateTime "
          + "WHERE sm_key = :smKey")
  public Mono<Integer> updateSettingMasterValueByKey(
      String smValue, LocalDateTime updateDateTime, String smKey);
}
