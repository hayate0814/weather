package com.jp.kinto.app.bff.model.user;

import com.jp.kinto.app.bff.core.constant.Constant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class UserInfoResponse {
  private Integer userId;
  private String memberId;
  //  private AuthUser.Role role;
  private Constant.MemberType memberType;
  private String memberName;
}
