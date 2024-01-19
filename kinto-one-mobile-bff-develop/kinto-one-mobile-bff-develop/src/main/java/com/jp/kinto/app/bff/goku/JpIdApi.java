package com.jp.kinto.app.bff.goku;

import static com.jp.kinto.app.bff.core.constant.Constant.*;
import static com.jp.kinto.app.bff.core.message.Msg.format;
import static com.jp.kinto.app.bff.utils.Tips.stringToBase64;

import com.jp.kinto.app.bff.core.properties.JpIdProperties;
import com.jp.kinto.app.bff.goku.client.JpIdClient;
import com.jp.kinto.app.bff.goku.response.JpIdAuthorizationInfoRes;
import com.jp.kinto.app.bff.goku.response.JpIdLoginRes;
import com.jp.kinto.app.bff.goku.response.JpIdMemberInfoRes;
import com.jp.kinto.app.bff.goku.response.JpidMembersRegisterCheckRes;
import com.jp.kinto.app.bff.model.registerCheck.MembersRegisterCheckRequest;
import java.util.HashMap;
import java.util.function.Function;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/** メンバー関連API */
@Component
public class JpIdApi {
  private JpIdClient jpIdClient;
  private JpIdProperties jpIdProperties;
  private String jpIDCredentialKey;

  public JpIdApi(Function<String, WebClient> webClientFun, JpIdProperties jpIdProperties) {
    this.jpIdProperties = jpIdProperties;
    this.jpIdClient = new JpIdClient(webClientFun.apply(jpIdProperties.getUrl()));
    this.jpIDCredentialKey =
        stringToBase64(format("{}:{}", jpIdProperties.getClientId(), jpIdProperties.getSecret()));
  }

  public Mono<JpIdLoginRes> login(String userName, String password) {
    var map = new HashMap<String, String>();
    map.put("userName", userName);
    map.put("password", password);
    return jpIdClient.callWithCookie(
        JpIdLoginRes.class, client -> client.post().uri("/platform/api/auth/login").bodyValue(map));
  }

  public Mono<JpidMembersRegisterCheckRes> membersRegisterCheck(
      MembersRegisterCheckRequest membersRegisterCheckReq) {
    return jpIdClient.call(
        JpidMembersRegisterCheckRes.class,
        client ->
            client
                .post()
                .uri("/platform/api/members/register-check")
                .header("Authorization", jpIDCredentialKey)
                .body(Mono.just(membersRegisterCheckReq), MembersRegisterCheckRequest.class));
  }

  public Mono<JpIdAuthorizationInfoRes> authorizationInfo(String jpnKintoIdAuth) {
    return jpIdClient.call(
        JpIdAuthorizationInfoRes.class,
        client ->
            client
                .get()
                .uri("/platform/api/auth/authorizationInfo")
                .header("Cookie", JPN_KINTO_ID_AUTH + "=" + jpnKintoIdAuth));
  }

  public Mono<JpIdMemberInfoRes> personal(String jpnKintoIdAuth, String memberId) {
    return jpIdClient.call(
        JpIdMemberInfoRes.class,
        client ->
            client
                .get()
                .uri(String.format("platform/api/members/personal/%s", memberId))
                .header("Cookie", JPN_KINTO_ID_AUTH + "=" + jpnKintoIdAuth));
  }
}
