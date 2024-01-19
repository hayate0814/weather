package com.jp.kinto.app.bff.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bff.jp-id")
public class JpIdProperties {
  private String url;
  private String clientId;
  private String secret;
}
