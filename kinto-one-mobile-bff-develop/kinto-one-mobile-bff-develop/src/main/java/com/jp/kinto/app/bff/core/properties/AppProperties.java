package com.jp.kinto.app.bff.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bff.app")
public class AppProperties {
  private String latestVersionAndroid;
  private String latestVersionIos;
  private String latestTermVersion;
  private String upgradeRequireAndroid;
  private String upgradeRequireIos;
}
