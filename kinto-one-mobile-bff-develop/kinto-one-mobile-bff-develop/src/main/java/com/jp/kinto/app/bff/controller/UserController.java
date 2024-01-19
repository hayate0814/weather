package com.jp.kinto.app.bff.controller;

import static com.jp.kinto.app.bff.core.constant.Constant.API_URL_PREFIX;

import com.jp.kinto.app.bff.model.auth.AuthUser;
import com.jp.kinto.app.bff.model.contracts.ContractsListResponse;
import com.jp.kinto.app.bff.model.login.LoginRequest;
import com.jp.kinto.app.bff.model.login.LoginResponse;
import com.jp.kinto.app.bff.model.logout.LogoutResponse;
import com.jp.kinto.app.bff.model.newDevice.NewDeviceResponse;
import com.jp.kinto.app.bff.model.notification.Notification;
import com.jp.kinto.app.bff.model.notification.NotificationDetail;
import com.jp.kinto.app.bff.model.ocr.OcrResponse;
import com.jp.kinto.app.bff.model.registerCheck.MembersRegisterCheckRequest;
import com.jp.kinto.app.bff.model.registerCheck.MembersRegisterCheckResponse;
import com.jp.kinto.app.bff.model.simulation.SimulationListResponse;
import com.jp.kinto.app.bff.model.simulation.SimulationRequest;
import com.jp.kinto.app.bff.model.simulation.SimulationResponse;
import com.jp.kinto.app.bff.model.user.RefreshTokenRes;
import com.jp.kinto.app.bff.model.user.UserInfoResponse;
import com.jp.kinto.app.bff.model.versionCheck.VersionCheckResponse;
import com.jp.kinto.app.bff.service.ContractsListService;
import com.jp.kinto.app.bff.service.NotificationService;
import com.jp.kinto.app.bff.service.OcrService;
import com.jp.kinto.app.bff.service.SimulationService;
import com.jp.kinto.app.bff.service.UserService;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping(API_URL_PREFIX)
public class UserController {
  @Autowired private UserService userService;

  @Autowired private NotificationService notificationService;

  @Autowired private SimulationService simulationService;

  @Autowired private ContractsListService contractsListService;

  @Autowired private OcrService ocrService;

  @PostMapping("/newDevice")
  public Mono<NewDeviceResponse> newDevice(@RequestParam("kindSv") Integer kindSv) {
    return userService.newDevice(kindSv);
  }

  @GetMapping("/versionCheck")
  public Mono<VersionCheckResponse> versionCheck(@RequestHeader("Version") String version) {
    return userService.versionCheck(version);
  }

  @GetMapping("/user")
  public Mono<UserInfoResponse> user(Authentication authentication) {
    var authUser = (AuthUser) authentication.getCredentials();
    return userService.getUserInfo(authUser);
  }

  @PostMapping("/login")
  public Mono<LoginResponse> login(
      Authentication authentication, @RequestBody LoginRequest loginRequest) {
    var authUser = (AuthUser) authentication.getCredentials();
    return userService.login(authUser, loginRequest);
  }

  @PostMapping("/logout")
  public Mono<LogoutResponse> logout(Authentication authentication) {
    var authUser = (AuthUser) authentication.getCredentials();
    return userService.logout(authUser);
  }

  @PostMapping("/refreshToken")
  public Mono<RefreshTokenRes> refreshToken(Authentication authentication) {
    var authUser = (AuthUser) authentication.getCredentials();
    return userService.getRefreshToken(authUser);
  }

  @PostMapping("/members/register-check")
  public Mono<MembersRegisterCheckResponse> membersRegisterCheck(
      @RequestBody MembersRegisterCheckRequest membersRegisterCheckReq) {
    return userService.membersRegisterCheck(membersRegisterCheckReq);
  }

  @PostMapping("/noticeToken")
  Mono<Void> saveNoticeToken(Authentication authentication, @RequestParam("token") String token) {
    var authUser = (AuthUser) authentication.getCredentials();
    return userService.saveNoticeToken(authUser.getUserId(), token);
  }

  @GetMapping("/noticeList")
  public Flux<Notification> getNotificationList(Authentication authentication) {
    return notificationService.getNotificationList((AuthUser) authentication.getCredentials());
  }

  @GetMapping("/notificationDetail/{notificationId}")
  public Mono<NotificationDetail> getNotificationDetail(
      Authentication authentication, @PathVariable Integer notificationId) {
    return notificationService.getNotificationDetail(
        (AuthUser) authentication.getCredentials(), notificationId);
  }

  @GetMapping("/numberUnReadNotification")
  public Mono<Integer> getNumberUnreadNotification(Authentication authentication) {
    return notificationService.getNumberUnreadNotification(
        (AuthUser) authentication.getCredentials());
  }

  @GetMapping("/memberDetail")
  public Mono<SimulationRequest.Personal> getMemberDetail(Authentication authentication) {
    return userService.getMemberDetail((AuthUser) authentication.getCredentials());
  }

  @PostMapping("/simulations")
  public Mono<Map<String, Object>> saveSimulation(
      Authentication authentication, @RequestBody SimulationRequest simulationRequest) {
    return simulationService.saveSimulation(
        (AuthUser) authentication.getCredentials(), simulationRequest);
  }

  @PutMapping("/simulations/{simulationId}")
  public Mono<Map<String, Object>> updateSimulation(
      Authentication authentication,
      @PathVariable Integer simulationId,
      @RequestBody SimulationRequest simulationRequest) {
    return simulationService.updateSimulation(
        (AuthUser) authentication.getCredentials(), simulationId, simulationRequest);
  }

  @GetMapping("/simulations")
  public Flux<SimulationListResponse> getSimulations(Authentication authentication) {
    return simulationService.getSimulationList((AuthUser) authentication.getCredentials());
  }

  @GetMapping("/simulations/{simulationId}")
  public Mono<SimulationResponse> getSimulation(
      Authentication authentication, @PathVariable Integer simulationId) {
    return simulationService.getSimulation(
        (AuthUser) authentication.getCredentials(), simulationId);
  }

  @DeleteMapping("/simulations/{simulationId}")
  public Mono<Void> deleteSimulation(
      Authentication authentication,
      @PathVariable Integer simulationId,
      @RequestParam String updateDatetime) {
    return simulationService.deleteSimulation(
        (AuthUser) authentication.getCredentials(), simulationId, updateDatetime);
  }

  @GetMapping("/contractsList")
  public Mono<List<ContractsListResponse>> getContractsList(Authentication authentication) {
    var authUser = (AuthUser) authentication.getCredentials();
    return contractsListService.getContractsList(authUser);
  }

  @PostMapping("/ocr")
  public Mono<OcrResponse> getOcrToken() {
    return ocrService.getOcrToken();
  }
}
