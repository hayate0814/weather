package com.jp.kinto.app.bff.model.login;

import com.jp.kinto.app.bff.core.constant.Constant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class LoginResponse {
  private Integer userId;
  private String memberId;
  //  private AuthUser.Role role;
  private Constant.MemberType memberType;
  private String token;
}
