package com.jp.kinto.app.bff.repository.entity;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("member_notification_history")
public class NotificationEntity {
  @Id
  @Column("notification_id")
  private Integer notificationId;

  @Column("member_id")
  private String memberId;

  @Column("old_contract_id")
  private String oldContractId;

  @Column("notification_kind_sv")
  private Integer notificationKindSv;

  @Column("title")
  private String title;

  @Column("content")
  private String content;

  @Column("read_flag")
  private Integer readFlag;

  @Column("create_datetime")
  private LocalDateTime createDatetime;
}
