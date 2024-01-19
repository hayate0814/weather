package com.jp.kinto.app.bff.simulation;

import static org.mockito.ArgumentMatchers.any;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jp.kinto.app.bff.ApiTest;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.core.security.jwt.JwtTokenProvider;
import com.jp.kinto.app.bff.goku.CarMasterGokuApi;
import com.jp.kinto.app.bff.goku.request.MonthlyPriceReq;
import com.jp.kinto.app.bff.goku.response.MonthlyPriceRes;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import com.jp.kinto.app.bff.model.simulation.SimulationRequest;
import com.jp.kinto.app.bff.repository.SimulationRepository;
import com.jp.kinto.app.bff.repository.UserDeviceRepository;
import com.jp.kinto.app.bff.repository.entity.Simulation;
import com.jp.kinto.app.bff.repository.entity.UserDevice;
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
@DisplayName("/api/simulationSaveTest")
public class SimulationSaveTest extends ApiTest {

  @MockBean private UserDeviceRepository userDeviceRepository;

  @MockBean private SimulationRepository simulationRepository;
  @Autowired private JwtTokenProvider jwtTokenProvider;

  @MockBean private CarMasterGokuApi gokuApi;

  @Autowired private ObjectMapper objectMapper;

  @Test
  @DisplayName("AuthUserのメンバIDが存在の正常系")
  void success_01() {
    MonthlyPriceReq request =
        MonthlyPriceReq.builder()
            .carModelId("CAR-0000001196")
            .contractMonths(84)
            .bonusAdditionAmount(100000)
            .planType("PLAN_A")
            .desiredNumberPlateRequested(true)
            .gradeId("GRD-0000005656")
            .packageId("PKG-0000021273")
            .outerPlateColorId("OPC-0000038079")
            .interiorColorId("ITC-0000007432")
            .packageExclusiveEquipmentIds(new String[] {"PED-0000009453", "PED-0000009455"})
            .packageEquipmentIds(new String[] {"PED-0000008435"})
            .singleOptionIds(
                new String[] {
                  "SOD-0000010778",
                  "SOD-0000010779",
                  "SOD-0000010780",
                  "SOD-0000010782",
                  "SOD-0000010783",
                  "SOD-0000010784",
                  "SOD-0000010785",
                  "SOD-0000010786"
                })
            .build();

    SimulationRequest simuReq = new SimulationRequest();

    SimulationRequest.SelectedCarSetting selectedCarSetting =
        new SimulationRequest.SelectedCarSetting();
    selectedCarSetting.setCarModelId("CAR-0000001196");
    selectedCarSetting.setPlanType("PLAN_A");
    selectedCarSetting.setDesiredNumberPlate1("7893");
    selectedCarSetting.setContractMonths(84);
    selectedCarSetting.setGradeId("GRD-0000005656");
    selectedCarSetting.setPackageId("PKG-0000021273");
    selectedCarSetting.setOuterPlateColorId("OPC-0000038079");
    selectedCarSetting.setInteriorColorId("ITC-0000007432");
    selectedCarSetting.setPackageExclusiveEquipmentIds(
        new String[] {"PED-0000009453", "PED-0000009455"});
    selectedCarSetting.setPackageEquipmentIds(new String[] {"PED-0000008435"});
    selectedCarSetting.setSingleOptionIds(
        new String[] {
          "SOD-0000010778",
          "SOD-0000010779",
          "SOD-0000010780",
          "SOD-0000010782",
          "SOD-0000010783",
          "SOD-0000010784",
          "SOD-0000010785",
          "SOD-0000010786"
        });
    selectedCarSetting.setBonusAdditionAmount(100000);
    simuReq.setSelectedCarSetting(selectedCarSetting);

    MonthlyPriceRes monthlyPriceRes =
        parseJsonFile(
            INPUT_ROOT + "/selection_detail/success_selection_detail.json",
            new TypeReference<>() {});

    Mockito.when(gokuApi.getMonthlyPrice(request)).thenReturn(Mono.just(monthlyPriceRes));

    UserDevice userDevice = new UserDevice();
    userDevice.setMemberId("2");
    userDevice.setUserId(2);
    Mockito.when(userDeviceRepository.findById(2)).thenReturn(Mono.just(userDevice));

    LocalDateTime now = LocalDateTime.now();
    Simulation simulation =
        new Simulation()
            .setGuestUserId(2)
            .setMemberId("2")
            .setSimulationDatetime(now)
            .setTitleJson("{}")
            .setSimulationJson("{}")
            .setUpdateDatetime(now)
            .setCreateDatetime(now);
    Mockito.when(simulationRepository.save(simulation)).thenReturn(Mono.just(simulation));

    Mockito.when(simulationRepository.findById(1)).thenReturn(Mono.just(simulation));

    webClient
        .post()
        .uri(uriBuilder -> uriBuilder.path("/api/simulations").build())
        .body(Mono.just(simuReq), SimulationRequest.class)
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

  @Test
  @DisplayName("SimulationIdが存在しません")
  void error_01() {
    UserDevice userDevice = new UserDevice();
    userDevice.setMemberId("2");
    userDevice.setUserId(2);
    Mockito.when(userDeviceRepository.findById(2)).thenReturn(Mono.just(userDevice));

    LocalDateTime now = LocalDateTime.now();
    Simulation simulation = new Simulation();
    simulation.setSimulationId(1);
    simulation.setMemberId("2");
    simulation.setUpdateDatetime(now);
    simulation.setSimulationDatetime(now);
    Mockito.when(simulationRepository.findByIdAndGuestUserId(any(), any()))
        .thenReturn(Mono.empty());

    Mockito.when(simulationRepository.deleteById(simulation.getSimulationId()))
        .thenReturn(Mono.empty());

    webClient
        .delete()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/api/simulations/2")
                    .queryParam("updateDatetime", now.toString())
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
        .isBadRequest()
        .expectBody()
        .json(
            """
            {"code":2,"message":"%s"}
            """
                .formatted(Msg.SimulationNotExist));
  }
}
