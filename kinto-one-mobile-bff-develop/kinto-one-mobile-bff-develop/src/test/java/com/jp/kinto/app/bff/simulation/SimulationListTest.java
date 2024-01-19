package com.jp.kinto.app.bff.simulation;

import static org.mockito.ArgumentMatchers.any;

import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.security.jwt.JwtTokenProvider;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import com.jp.kinto.app.bff.model.auth.AuthUser.Role;
import com.jp.kinto.app.bff.repository.SimulationRepository;
import com.jp.kinto.app.bff.repository.UserDeviceRepository;
import com.jp.kinto.app.bff.repository.entity.Simulation;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

@SpringBootTest
@DisplayName("/api/simulations")
public class SimulationListTest extends ApiTest {

  @MockBean private SimulationRepository simulationRepository;
  @MockBean private UserDeviceRepository userDeviceRepository;
  @Autowired private JwtTokenProvider jwtTokenProvider;

  @Test
  @DisplayName("AuthUserのユーザIDが存在の正常系")
  void success_01() {
    Simulation simulation = new Simulation();
    simulation.setSimulationId(1);
    simulation.setSimulationDatetime(LocalDateTime.now());
    simulation.setUpdateDatetime(LocalDateTime.now());
    simulation.setTitleJson(
        "{\"carModelName\":\"ルーミー\",\"gradeName\":\"X GAS 1.0L"
            + " 2WD(5人)\",\"carImageUrl\":\"https://kinto-jp.com/s3resource/carmodel/ROM005X07.png\"}");

    Simulation simulation2 = new Simulation();
    simulation2.setSimulationId(2);
    simulation2.setSimulationDatetime(LocalDateTime.now());
    simulation2.setUpdateDatetime(LocalDateTime.now());
    simulation2.setTitleJson(
        "{\"carModelName\":\"ヤリス\",\"gradeName\":\"X GAS 1.5L"
            + " 4WD(5人)\",\"carImageUrl\":\"https://kinto-jp.com/s3resource/carmodel/YRA002202.png\"}");

    Mockito.when(simulationRepository.findByGuestUserId(any(Integer.class), any(Integer.class)))
        .thenReturn(Flux.just(simulation, simulation2));
    webClient
        .get()
        .uri("/api/simulations")
        .header(
            "Authorization",
            "Bearer "
                + jwtTokenProvider.createToken(
                    AuthUser.builder().userId(1).memberId("aaa").role(Role.User).build()))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .isOk();
  }

  @Test
  @DisplayName("データ取得失敗")
  void failure_01() {
    Mockito.when(userDeviceRepository.findById(1)).thenThrow(new RuntimeException("test"));
    webClient
        .get()
        .uri("/api/simulations")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectAll(printResponse)
        .expectStatus()
        .is5xxServerError();
  }
}
