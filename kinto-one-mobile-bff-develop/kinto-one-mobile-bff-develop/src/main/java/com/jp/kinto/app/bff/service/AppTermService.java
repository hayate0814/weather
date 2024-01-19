package com.jp.kinto.app.bff.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AppTermService {

  private String termText;

  public AppTermService() {
    try (val is = new ClassPathResource("html/appTerm.html").getInputStream()) {
      BufferedReader inputStreamReader =
          new BufferedReader(new InputStreamReader(is, Charset.forName("utf8")));
      StringBuilder sb = new StringBuilder();
      char[] c = new char[1024];
      for (int length = inputStreamReader.read(c); length != -1; ) {
        sb.append(c, 0, length);
        length = inputStreamReader.read(c);
      }
      termText = sb.toString();
    } catch (Exception e) {
      throw new RuntimeException("Failed to read term file(html/appTerm.html)", e);
    }
  }

  public String getAppTerm() {
    return termText;
  }
}
