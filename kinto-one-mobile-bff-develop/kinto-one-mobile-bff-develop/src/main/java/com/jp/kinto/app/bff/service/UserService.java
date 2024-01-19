package com.jp.kinto.app.bff.service;

import static com.jp.kinto.app.bff.core.constant.Constant.GUEST_NAME;
import static com.jp.kinto.app.bff.core.constant.Constant.JPN_KINTO_ID_AUTH;
import static com.jp.kinto.app.bff.core.message.Msg.format;

import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.core.constant.Constant.DeviceKindSv;
import com.jp.kinto.app.bff.core.exception.BadRequestException;
import com.jp.kinto.app.bff.core.exception.MessageException;
import com.jp.kinto.app.bff.core.exception.UnauthorizedException;
import com.jp.kinto.app.bff.core.message.Msg;
import com.jp.kinto.app.bff.core.properties.AppProperties;
import com.jp.kinto.app.bff.core.security.jwt.JwtTokenProvider;
import com.jp.kinto.app.bff.goku.JpIdApi;
import com.jp.kinto.app.bff.model.AppVersion;
import com.jp.kinto.app.bff.model.auth.AuthUser;
import com.jp.kinto.app.bff.model.login.LoginRequest;
import com.jp.kinto.app.bff.model.login.LoginResponse;
import com.jp.kinto.app.bff.model.logout.LogoutResponse;
import com.jp.kinto.app.bff.model.newDevice.NewDeviceResponse;
import com.jp.kinto.app.bff.model.registerCheck.MembersRegisterCheckRequest;
import com.jp.kinto.app.bff.model.registerCheck.MembersRegisterCheckResponse;
import com.jp.kinto.app.bff.model.simulation.SimulationRequest;
import com.jp.kinto.app.bff.model.user.RefreshTokenRes;
import com.jp.kinto.app.bff.model.user.UserInfoResponse;
import com.jp.kinto.app.bff.model.versionCheck.VersionCheckResponse;
import com.jp.kinto.app.bff.repository.LoginHistoryRepository;
import com.jp.kinto.app.bff.repository.UserDeviceRepository;
import com.jp.kinto.app.bff.repository.entity.LoginHistory;
import com.jp.kinto.app.bff.repository.entity.UserDevice;
import com.jp.kinto.app.bff.utils.Asserts;
import io.jsonwebtoken.lang.Collections;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserService {

  @Autowired private JwtTokenProvider jwtTokenProvider;
  @Autowired private UserDeviceRepository userDeviceRepository;
  @Autowired private JpIdApi jpIdApi;
  @Autowired private AppProperties appProperties;
  @Autowired private LoginHistoryRepository loginHistoryRepository;

  /**
   * ユーザー端末登録
   *
   * @param deviceKindSv 端末種別
   * @return NewDeviceResponse
   */
  @Transactional
  public Mono<NewDeviceResponse> newDevice(Integer deviceKindSv) {
    // 端末種別の有効チェック
    Asserts.isValidSystemValue(
        DeviceKindSv.class,
        deviceKindSv,
        () -> new MessageException(Msg.invalidValue.args("端末種別")));
    var now = LocalDateTime.now();
    var deviceCode = String.valueOf(UUID.randomUUID());
    UserDevice userDevice =
        new UserDevice()
            .setDeviceCode(deviceCode)
            .setDeviceKindSv(deviceKindSv)
            .setUpdateDatetime(now)
            .setCreateDatetime(now);

    return userDeviceRepository
        .save(userDevice)
        .map(
            device ->
                NewDeviceResponse.builder()
                    .userId(device.getUserId())
                    .token(
                        jwtTokenProvider.createToken(
                            AuthUser.builder()
                                .userId(device.getUserId())
                                .role(AuthUser.Role.User)
                                .build()))
                    .deviceCode(deviceCode)
                    .build());
  }

  /**
   * 通知トークン更新
   *
   * @param userId ユーザーID
   * @param token 通知トークン
   * @return Void
   */
  @Transactional
  public Mono<Void> saveNoticeToken(Integer userId, String token) {
    // 入力チェック
    Asserts.notNull(userId, () -> new MessageException(Msg.required.args("ユーザーId")));
    Asserts.notEmptyText(token, () -> new MessageException(Msg.required.args("通知トークン")));
    return userDeviceRepository
        .updateNoticeToken(userId, token, LocalDateTime.now())
        .flatMap(
            count -> {
              Asserts.assertTrue(
                  count == 1, format("通知トークンの更新処理が失敗しました。user_id:{}, token:{}", userId, token));
              return Mono.empty();
            });
  }

  /**
   * アプリVersionチェック
   *
   * @return VersionCheckResponse
   */
  public Mono<VersionCheckResponse> versionCheck(String version) {
    var appVersion =
        AppVersion.fromString(version)
            .orElseThrow(() -> new MessageException(format(Msg.invalidValue.args("Versionヘッダー"))));

    String latestVersion = null;
    String upgradeRequireLimit = null;
    switch (appVersion.getDeviceKindSv()) {
      case Android -> {
        latestVersion = appProperties.getLatestVersionAndroid();
        upgradeRequireLimit = appProperties.getUpgradeRequireAndroid();
      }
      case Ios -> {
        latestVersion = appProperties.getLatestVersionIos();
        upgradeRequireLimit = appProperties.getUpgradeRequireIos();
      }
    }

    Asserts.notNull(latestVersion, "設定ファイルにLatestVersionが設定されない");
    Asserts.notNull(upgradeRequireLimit, "設定ファイルに下限Versionが設定されない");
    return Mono.just(
        VersionCheckResponse.builder()
            .latestVersion(latestVersion)
            .upgradeRequire(appVersion.getVersion().compareTo(upgradeRequireLimit) < 0)
            .latestTermVersion(appProperties.getLatestTermVersion())
            .build());
  }

  /**
   * ログイン処理
   *
   * @param authUser ユーザー
   * @param loginRequest ログイン情報
   * @return LoginResponse
   */
  @Transactional
  public Mono<LoginResponse> login(AuthUser authUser, LoginRequest loginRequest) {
    Asserts.assertTrue(
        !StringUtils.isEmpty(loginRequest.getEmail())
            && !StringUtils.isEmpty(loginRequest.getPassword()),
        () -> new MessageException(Msg.required.args("メールアドレスとパスワード")));
    return jpIdApi
        .login(loginRequest.getEmail(), loginRequest.getPassword())
        .flatMap(
            data -> {
              MultiValueMap<String, ResponseCookie> cookies = data.getCookies();
              if (log.isDebugEnabled()) {
                for (Map.Entry<String, List<ResponseCookie>> en : cookies.entrySet()) {
                  log.debug(
                      "cookie-{}:{}",
                      en.getKey(),
                      String.join(";", en.getValue().stream().map(HttpCookie::getValue).toList()));
                }
              }
              List<ResponseCookie> jpnKintoIdAuthCookies = cookies.get(JPN_KINTO_ID_AUTH);
              Asserts.notEmpty(
                  jpnKintoIdAuthCookies, "会員プラットフォームからcookie取得できません。cookie:" + JPN_KINTO_ID_AUTH);
              String jpnKintoIdAuth = jpnKintoIdAuthCookies.get(0).getValue();
              log.debug("cookie-{}:{}", JPN_KINTO_ID_AUTH, jpnKintoIdAuth);
              return jpIdApi
                  .authorizationInfo(jpnKintoIdAuth)
                  .flatMap(
                      mem ->
                          loginHistoryRepository
                              .findOneByUserAndMemberId(authUser.getUserId(), mem.getId())
                              .flatMap(
                                  existingLoginHistory -> {
                                    existingLoginHistory.setLoginDatetime(LocalDateTime.now());
                                    return loginHistoryRepository.save(existingLoginHistory);
                                  })
                              .switchIfEmpty(
                                  loginHistoryRepository.save(
                                      new LoginHistory()
                                          .setUserId(authUser.getUserId())
                                          .setMemberId(mem.getId())
                                          .setLoginDatetime(LocalDateTime.now())))
                              .then(
                                  userDeviceRepository
                                      .updateMemberId(
                                          authUser.getUserId(), mem.getId(), LocalDateTime.now())
                                      .map(
                                          count -> {
                                            Asserts.assertTrue(
                                                count == 1,
                                                format(
                                                    "メンバーIDの更新処理が失敗しました。user_id:{}, member_id:{}",
                                                    authUser.getUserId(),
                                                    mem.getId()));
                                            if (Constant.MemberType.fromString(mem.getType())
                                                == Constant.MemberType.Corporate) {
                                              throw new BadRequestException(
                                                  BadRequestException.ErrorCode.LogicMessage,
                                                  Msg.CorporationUser);
                                            }
                                            return LoginResponse.builder()
                                                .userId(authUser.getUserId())
                                                .memberId(mem.getId())
                                                .memberType(
                                                    Constant.MemberType.fromString(mem.getType()))
                                                .token(
                                                    jwtTokenProvider.createToken(
                                                        AuthUser.builder()
                                                            .userId(authUser.getUserId())
                                                            .memberId(mem.getId())
                                                            .role(AuthUser.Role.Member)
                                                            .jpnKintoIdAuth(jpnKintoIdAuth)
                                                            .memberType(
                                                                Constant.MemberType.fromString(
                                                                    mem.getType()))
                                                            .build()))
                                                .build();
                                          })));
            });
  }

  /**
   * ログアウト
   *
   * @param authUser ユーザー
   * @return LogoutResponse
   */
  public Mono<LogoutResponse> logout(AuthUser authUser) {
    return userDeviceRepository
        .updateMemberId(authUser.getUserId(), null, LocalDateTime.now())
        .map(
            count -> {
              Asserts.assertTrue(
                  count == 1, format("ログアウト処理が失敗しました。user_id:{}", authUser.getUserId()));
              return LogoutResponse.builder()
                  .token(
                      jwtTokenProvider.createToken(
                          AuthUser.builder()
                              .userId(authUser.getUserId())
                              .role(AuthUser.Role.User)
                              .build()))
                  .build();
            });
  }

  /**
   * リフレッシュトークン取得
   *
   * @param authUser ユーザー
   * @return RefreshTokenRes
   */
  public Mono<RefreshTokenRes> getRefreshToken(AuthUser authUser) {
    return Mono.just(
        RefreshTokenRes.builder()
            .token(
                jwtTokenProvider.createToken(
                    AuthUser.builder()
                        .userId(authUser.getUserId())
                        .memberId(null)
                        .role(AuthUser.Role.User)
                        .jpnKintoIdAuth(null)
                        .memberType(null)
                        .build()))
            .build());
  }

  /**
   * ユーザ情報取得
   *
   * @param authUser AuthUser
   * @return Mono<UserInfoResponse>
   */
  public Mono<UserInfoResponse> getUserInfo(AuthUser authUser) {
    if (authUser.getJpnKintoIdAuth() == null || authUser.getMemberId() == null) {
      return Mono.just(
          UserInfoResponse.builder()
              .userId(authUser.getUserId())
              .memberId(null)
              .memberType(authUser.getMemberType())
              .memberName(GUEST_NAME)
              .build());
    }

    return jpIdApi
        .personal(authUser.getJpnKintoIdAuth(), authUser.getMemberId())
        .map(
            res -> {
              String memberName =
                  Optional.of(res.getPersonalMember())
                      .filter(
                          member ->
                              member.getFirstName() != null
                                  && !member.getFirstName().isEmpty()
                                  && member.getLastName() != null
                                  && !member.getLastName().isEmpty())
                      .map(
                          member ->
                              String.format("%s %s", member.getLastName(), member.getFirstName()))
                      .orElse(res.getEmail());

              return UserInfoResponse.builder()
                  .userId(authUser.getUserId())
                  .memberId(authUser.getMemberId())
                  .memberType(authUser.getMemberType())
                  .memberName(memberName)
                  .build();
            });
  }

  public Mono<SimulationRequest.Personal> getMemberDetail(AuthUser authUserParam) {
    return Mono.just(authUserParam.getUserId())
        .flatMap(
            userId -> {
              UserDevice userDevice = new UserDevice();
              // ユーザデバイス検索(メンバ情報取得)
              if (Optional.ofNullable(authUserParam.getMemberId()).isEmpty()) {
                return userDeviceRepository.findById(userId);
              } else {
                return Mono.just(userDevice.setMemberId(authUserParam.getMemberId()));
              }
            })
        .flatMap(
            userDevice -> {
              // ユーザ判断
              SimulationRequest.Personal personal = new SimulationRequest.Personal();
              if (Optional.ofNullable(userDevice.getMemberId()).isPresent()
                  && Optional.ofNullable(authUserParam.getJpnKintoIdAuth()).isPresent()) {

                return jpIdApi
                    .personal(authUserParam.getJpnKintoIdAuth(), userDevice.getMemberId())
                    .map(
                        jpIdMemberInfoRes -> {
                          Optional.ofNullable(jpIdMemberInfoRes.getPersonalMember())
                              .ifPresent(
                                  jpIdMemberInfo -> {
                                    personal.setLastName(
                                        StringUtils.trimToNull(jpIdMemberInfo.getLastName()));
                                    personal.setFirstName(
                                        StringUtils.trimToNull(jpIdMemberInfo.getFirstName()));
                                    personal.setLastNameKana(
                                        StringUtils.trimToNull(jpIdMemberInfo.getLastNameKana()));
                                    personal.setFirstNameKana(
                                        StringUtils.trimToNull(jpIdMemberInfo.getFirstNameKana()));
                                    personal.setBirthday(
                                        StringUtils.trimToNull(jpIdMemberInfo.getBirthday()));
                                    personal.setGender(
                                        StringUtils.trimToNull(jpIdMemberInfo.getGender()));
                                    personal.setPostalCode(
                                        StringUtils.trimToNull(jpIdMemberInfo.getPostalCode()));
                                    personal.setPrefecture(
                                        StringUtils.trimToNull(jpIdMemberInfo.getPrefecture()));
                                    personal.setCity(
                                        StringUtils.trimToNull(jpIdMemberInfo.getCity()));
                                    personal.setStreet(
                                        StringUtils.trimToNull(jpIdMemberInfo.getStreet()));
                                    personal.setStreetKana(
                                        StringUtils.trimToNull(jpIdMemberInfo.getStreetKana()));
                                    personal.setAddressOther(
                                        StringUtils.trimToNull(jpIdMemberInfo.getAddressOther()));
                                    personal.setEmail(
                                        StringUtils.trimToNull(jpIdMemberInfoRes.getEmail()));
                                    personal.setPhoneNumber(
                                        StringUtils.trimToNull(jpIdMemberInfo.getPhoneNumber()));
                                    personal.setMobilePhoneNumber(
                                        StringUtils.trimToNull(
                                            jpIdMemberInfo.getMobilePhoneNumber()));
                                    personal.setPreferredContactPhoneNumber(
                                        StringUtils.trimToNull(
                                            jpIdMemberInfo.getPreferredContactPhoneNumber()));
                                    personal.setPreferredContactTimezones(
                                        Collections.isEmpty(
                                                jpIdMemberInfo.getPreferredContactTimezones())
                                            ? null
                                            : jpIdMemberInfo.getPreferredContactTimezones());
                                    personal.setResidenceType(
                                        StringUtils.trimToNull(jpIdMemberInfo.getResidenceType()));
                                    personal.setResidenceYears(
                                        StringUtils.trimToNull(jpIdMemberInfo.getResidenceYears()));
                                    personal.setIsPrimaryIncomeProvider(
                                        StringUtils.trimToNull(
                                            jpIdMemberInfo.getIsPrimaryIncomeProvider()));
                                    personal.setHasHousingLoan(
                                        StringUtils.trimToNull(jpIdMemberInfo.getHasHousingLoan()));
                                    personal.setHasSpouse(
                                        StringUtils.trimToNull(jpIdMemberInfo.getHasSpouse()));
                                    personal.setNumberOfHousemates(
                                        99 == jpIdMemberInfo.getNumberOfHousemates()
                                            ? null
                                            : jpIdMemberInfo.getNumberOfHousemates());
                                    personal.setNumberOfChildren(
                                        99 == jpIdMemberInfo.getNumberOfChildren()
                                            ? null
                                            : jpIdMemberInfo.getNumberOfChildren());
                                    personal.setAnnualIncome(
                                        99 == jpIdMemberInfo.getAnnualIncome()
                                            ? null
                                            : jpIdMemberInfo.getAnnualIncome());

                                    Optional.ofNullable(jpIdMemberInfo.getOffice())
                                        .ifPresent(
                                            jpIdOfficeInfo -> {
                                              personal.setOffice(new SimulationRequest.Office());
                                              personal
                                                  .getOffice()
                                                  .setJob(
                                                      StringUtils.trimToNull(
                                                          jpIdOfficeInfo.getJob()));
                                              personal
                                                  .getOffice()
                                                  .setWorkplaceBusinessStructure(
                                                      StringUtils.trimToNull(
                                                          jpIdOfficeInfo
                                                              .getWorkplaceBusinessStructure()));
                                              personal
                                                  .getOffice()
                                                  .setWorkplaceBusinessStructureAffix(
                                                      StringUtils.trimToNull(
                                                          jpIdOfficeInfo
                                                              .getWorkplaceBusinessStructureAffix()));
                                              personal
                                                  .getOffice()
                                                  .setWorkplaceName(
                                                      StringUtils.trimToNull(
                                                          jpIdOfficeInfo.getWorkplaceName()));
                                              personal
                                                  .getOffice()
                                                  .setWorkplaceNameKana(
                                                      StringUtils.trimToNull(
                                                          jpIdOfficeInfo.getWorkplaceNameKana()));
                                              personal
                                                  .getOffice()
                                                  .setWorkplaceDepartment(
                                                      StringUtils.trimToNull(
                                                          jpIdOfficeInfo.getWorkplaceDepartment()));
                                              personal
                                                  .getOffice()
                                                  .setWorkplacePosition(
                                                      StringUtils.trimToNull(
                                                          jpIdOfficeInfo.getWorkplacePosition()));
                                              personal
                                                  .getOffice()
                                                  .setWorkplacePostalCode(
                                                      StringUtils.trimToNull(
                                                          jpIdOfficeInfo.getWorkplacePostalCode()));
                                              personal
                                                  .getOffice()
                                                  .setWorkplacePrefecture(
                                                      StringUtils.trimToNull(
                                                          jpIdOfficeInfo.getWorkplacePrefecture()));
                                              personal
                                                  .getOffice()
                                                  .setWorkplaceCity(
                                                      StringUtils.trimToNull(
                                                          jpIdOfficeInfo.getWorkplaceCity()));
                                              personal
                                                  .getOffice()
                                                  .setWorkplaceStreet(
                                                      StringUtils.trimToNull(
                                                          jpIdOfficeInfo.getWorkplaceStreet()));
                                              personal
                                                  .getOffice()
                                                  .setWorkplaceStreetKana(
                                                      StringUtils.trimToNull(
                                                          jpIdOfficeInfo.getWorkplaceStreetKana()));
                                              personal
                                                  .getOffice()
                                                  .setWorkplaceAddressOther(
                                                      StringUtils.trimToNull(
                                                          jpIdOfficeInfo
                                                              .getWorkplaceAddressOther()));
                                              personal
                                                  .getOffice()
                                                  .setWorkplacePhoneNumber(
                                                      StringUtils.trimToNull(
                                                          jpIdOfficeInfo
                                                              .getWorkplacePhoneNumber()));
                                              personal
                                                  .getOffice()
                                                  .setWorkplaceHeadOfficeLocation(
                                                      StringUtils.trimToNull(
                                                          jpIdOfficeInfo
                                                              .getWorkplaceHeadOfficeLocation()));
                                              personal
                                                  .getOffice()
                                                  .setWorkplaceContinuousYears(
                                                      StringUtils.trimToNull(
                                                          jpIdOfficeInfo
                                                              .getWorkplaceContinuousYears()));
                                              personal
                                                  .getOffice()
                                                  .setWorkplaceNumberOfEmployees(
                                                      StringUtils.trimToNull(
                                                          jpIdOfficeInfo
                                                              .getWorkplaceNumberOfEmployees()));
                                              if (!personal.getOffice().hasAllAttributes()) {
                                                personal.setOffice(null);
                                              }
                                            });
                                  });
                          return personal;
                        });
              } else {
                throw UnauthorizedException.MEMBER_UNAUTHORIZED;
              }
            });
  }

  /**
   * メールアドレス登録チェック
   *
   * @param request メールアドレス情報
   * @return MembersRegisterCheckResponse
   */
  public Mono<MembersRegisterCheckResponse> membersRegisterCheck(
      MembersRegisterCheckRequest request) {
    Asserts.assertTrue(
        !StringUtils.isEmpty(request.getEmail()),
        () -> new MessageException(Msg.required.args("メールアドレス")));
    return jpIdApi
        .membersRegisterCheck(request)
        .map(data -> MembersRegisterCheckResponse.builder().register(data.getRegister()).build());
  }
}
