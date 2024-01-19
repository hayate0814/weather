package com.jp.kinto.app.bff.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bff.goku")
public class GokuProperties {
  private String url;
  private String cardataUrl;
  private String insertMessageUrl;
}
