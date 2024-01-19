package com.jp.kinto.app.bff.goku.response;

import lombok.Data;

@Data
public class JpIdAuthorizationInfoRes {
  private String id;
  private String type;
  private String email;
  private boolean mailActive;
  private boolean memberShip;
}
