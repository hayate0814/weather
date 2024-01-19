package com.jp.kinto.app.bff.repository.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@Table("simulation")
public class Simulation {
  @Id
  @Column("simulation_id")
  private Integer simulationId;

  @Column("guest_user_id")
  private Integer guestUserId;

  @Column("member_id")
  private String memberId;

  @Column("simulation_datetime")
  private LocalDateTime simulationDatetime;

  @Column("title_json")
  private String titleJson;

  @Column("simulation_json")
  private String simulationJson;

  @Column("update_datetime")
  private LocalDateTime updateDatetime;

  @Column("create_datetime")
  private LocalDateTime createDatetime;
}
