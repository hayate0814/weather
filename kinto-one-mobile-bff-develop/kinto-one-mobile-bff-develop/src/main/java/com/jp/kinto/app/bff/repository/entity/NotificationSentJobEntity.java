package com.jp.kinto.app.bff.repository.entity;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("notification_sent_job")
public class NotificationSentJobEntity {
  @Id
  @Column("id")
  private Integer id;

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

  @Column("sent_flag")
  private Integer sentFlag;

  @Column("update_datetime")
  private LocalDateTime updateDateTime;

  @Column("create_datetime")
  private LocalDateTime createDateTime;
}
