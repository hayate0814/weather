package com.jp.kinto.app.bff.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bff.ocr")
public class OCRProperties {
  private String url;
  private String user;
  private String pass;
}
