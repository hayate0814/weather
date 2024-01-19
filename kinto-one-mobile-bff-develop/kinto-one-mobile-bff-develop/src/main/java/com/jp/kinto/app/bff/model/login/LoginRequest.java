package com.jp.kinto.app.bff.model.login;

import lombok.Data;

@Data
public class LoginRequest {
  private String email;
  private String password;
}
