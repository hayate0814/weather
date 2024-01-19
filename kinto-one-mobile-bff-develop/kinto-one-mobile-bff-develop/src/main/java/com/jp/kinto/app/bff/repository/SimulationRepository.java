package com.jp.kinto.app.bff.repository;

import com.jp.kinto.app.bff.repository.entity.Simulation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SimulationRepository extends ReactiveCrudRepository<Simulation, Integer> {

  @Query(
      "SELECT simulation_id, simulation_datetime, JSON_PRETTY(title_json) AS title_json,"
          + " update_datetime FROM simulation WHERE guest_user_id = :guestUserId order by"
          + " simulation_id desc limit :limit")
  public Flux<Simulation> findByGuestUserId(Integer guestUserId, Integer limit);

  @Query(
      "SELECT simulation_id, simulation_datetime, JSON_PRETTY(title_json) AS title_json,"
          + " update_datetime FROM simulation WHERE member_id = :memberId order by simulation_id"
          + " desc limit :limit")
  public Flux<Simulation> findByMemberId(String memberId, Integer limit);

  @Query(
      "SELECT simulation_id, guest_user_id, member_id, simulation_datetime, JSON_PRETTY(title_json)"
          + " AS title_json, JSON_PRETTY(simulation_json) AS simulation_json, update_datetime,"
          + " create_datetime FROM simulation WHERE simulation_id = :id AND guest_user_id ="
          + " :guestUserId")
  public Mono<Simulation> findByIdAndGuestUserId(Integer id, Integer guestUserId);

  @Query(
      "SELECT simulation_id, guest_user_id, member_id, simulation_datetime, JSON_PRETTY(title_json)"
          + " AS title_json, JSON_PRETTY(simulation_json) AS simulation_json, update_datetime,"
          + " create_datetime FROM simulation WHERE simulation_id = :id AND member_id = :memberId")
  public Mono<Simulation> findByIdAndMemberId(Integer id, String memberId);
}
