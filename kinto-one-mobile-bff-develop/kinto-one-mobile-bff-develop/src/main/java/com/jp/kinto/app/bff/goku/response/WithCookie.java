package com.jp.kinto.app.bff.goku.response;

import org.springframework.http.ResponseCookie;
import org.springframework.util.MultiValueMap;

public interface WithCookie {

  MultiValueMap<String, ResponseCookie> getCookies();

  void setCookies(MultiValueMap<String, ResponseCookie> cookies);
}
