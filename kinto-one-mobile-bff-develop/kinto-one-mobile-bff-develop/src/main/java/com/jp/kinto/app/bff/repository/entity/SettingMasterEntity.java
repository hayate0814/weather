package com.jp.kinto.app.bff.repository.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@Table("setting_master")
public class SettingMasterEntity {
  @Column("sm_key")
  private String smKey;

  @Column("sm_value")
  private String smValue;

  @Column("create_datetime")
  private LocalDateTime createDateTime;

  @Column("update_datetime")
  private LocalDateTime updateDateTime;
}
