package com.jp.kinto.app.bff.goku.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.http.ResponseCookie;
import org.springframework.util.MultiValueMap;

@Data
public class JpIdLoginRes implements WithCookie {
  private String redirectUrl;
  @JsonIgnore private MultiValueMap<String, ResponseCookie> cookies;

  @Override
  public MultiValueMap<String, ResponseCookie> getCookies() {
    return cookies;
  }
}
