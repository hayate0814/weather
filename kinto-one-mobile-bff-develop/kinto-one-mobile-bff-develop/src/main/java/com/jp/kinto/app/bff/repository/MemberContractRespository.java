package com.jp.kinto.app.bff.repository;

import com.jp.kinto.app.bff.repository.entity.MemberContract;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MemberContractRespository extends ReactiveCrudRepository<MemberContract, Integer> {

  @Query(
      "SELECT * FROM member_contract WHERE old_contract_id = :oldContractId "
          + "AND member_id =:memberId ")
  public Mono<MemberContract> findMemberContractById(String oldContractId, String memberId);
}
