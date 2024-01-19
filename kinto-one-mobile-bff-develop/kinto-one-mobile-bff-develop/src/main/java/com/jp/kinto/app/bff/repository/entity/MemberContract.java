package com.jp.kinto.app.bff.repository.entity;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Accessors(chain = true)
@Table("member_contract")
public class MemberContract {
  @Column("contract_id")
  private String contractId;

  @Column("application_id")
  private Integer applicationId;

  @Column("member_id")
  private String memberId;

  @Column("old_contract_id")
  private String oldContractId;

  @Column("update_datetime")
  private LocalDateTime updateDatetime;

  @Column("create_datetime")
  private LocalDateTime createDatetime;
}
