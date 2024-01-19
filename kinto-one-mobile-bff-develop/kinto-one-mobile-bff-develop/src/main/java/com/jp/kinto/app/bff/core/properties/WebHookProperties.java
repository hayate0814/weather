package com.jp.kinto.app.bff.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bff.notice")
public class WebHookProperties {
  private String mobileApiAccessKey;
}
