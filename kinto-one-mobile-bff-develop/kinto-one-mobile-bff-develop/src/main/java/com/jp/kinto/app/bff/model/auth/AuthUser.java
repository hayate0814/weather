package com.jp.kinto.app.bff.model.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jp.kinto.app.bff.core.constant.Constant;
import java.security.Principal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class AuthUser implements Principal {
  private Integer userId;
  private String memberId;
  private String deviceCode;
  private String token;
  private Role role;
  private Constant.MemberType memberType;
  private String jpnKintoIdAuth;

  //  private LocalDateTime createTime;

  @Override
  @JsonIgnore
  public String getName() {
    return "";
  }

  public static enum Role {
    User,
    Member
  }
}
