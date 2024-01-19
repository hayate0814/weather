package com.jp.kinto.app.bff.repository.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@Table("login_history")
public class LoginHistory {

  @Id
  @Column("login_history_id")
  private Integer loginHistoryId;

  @Column("user_id")
  private Integer userId;

  @Column("member_id")
  private String memberId;

  @Column("login_datetime")
  private LocalDateTime loginDatetime;
}
