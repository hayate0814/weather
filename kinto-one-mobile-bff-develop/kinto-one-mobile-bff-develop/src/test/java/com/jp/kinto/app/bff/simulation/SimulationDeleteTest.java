package com.jp.kinto.app.bff.simulation;

import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.security.jwt.JwtTokenProvider;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import com.jp.kinto.app.bff.repository.SimulationRepository;
import com.jp.kinto.app.bff.repository.entity.Simulation;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

@SpringBootTest
@DisplayName("/api/simulationDelete")
public class SimulationDeleteTest extends ApiTest {

  @MockBean private SimulationRepository simulationRepository;
  @Autowired private JwtTokenProvider jwtTokenProvider;

  @Test
  @DisplayName("SimulationIdが存在する場合：正常系")
  void success_01() {

    LocalDateTime updateTime = LocalDateTime.of(2023, 8, 9, 12, 12, 10);
    Simulation simulation = new Simulation();
    simulation.setSimulationId(1);
    simulation.setMemberId("2");
    simulation.setUpdateDatetime(updateTime);
    simulation.setSimulationDatetime(updateTime);
    Mockito.when(simulationRepository.findByIdAndGuestUserId(1, 2))
        .thenReturn(Mono.just(simulation));

    Mockito.when(simulationRepository.deleteById(simulation.getSimulationId()))
        .thenReturn(Mono.empty());

    webClient
        .delete()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/api/simulations/1")
                    .queryParam("updateDatetime", updateTime.toString())
                    .build())
        .header(
            "Authorization",
            "Bearer "
                + jwtTokenProvider.createToken(
                    AuthUser.builder().userId(2).memberId("2").role(AuthUser.Role.User).build()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("SimulationIdが存在しません")
  void error_01() {

    Mockito.when(simulationRepository.findByIdAndGuestUserId(1, 2)).thenReturn(Mono.empty());

    webClient
        .delete()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/api/simulations/2")
                    .queryParam("updateDatetime", LocalDateTime.now().toString())
                    .build())
        .header(
            "Authorization",
            "Bearer "
                + jwtTokenProvider.createToken(
                    AuthUser.builder().userId(2).memberId("2").role(AuthUser.Role.User).build()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError();
  }
}
