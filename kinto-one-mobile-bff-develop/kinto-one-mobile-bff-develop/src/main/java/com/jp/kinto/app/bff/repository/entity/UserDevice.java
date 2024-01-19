package com.jp.kinto.app.bff.repository.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@Table("user_device")
public class UserDevice {
  @Id
  @Column("user_id")
  private Integer userId;

  @Column("device_code")
  private String deviceCode;

  @Column("device_kind_sv")
  private Integer deviceKindSv;

  @Column("notice_token")
  private String noticeToken;

  @Column("member_id")
  private String memberId;

  @Column("update_datetime")
  private LocalDateTime updateDatetime;

  @Column("create_datetime")
  private LocalDateTime createDatetime;
}
