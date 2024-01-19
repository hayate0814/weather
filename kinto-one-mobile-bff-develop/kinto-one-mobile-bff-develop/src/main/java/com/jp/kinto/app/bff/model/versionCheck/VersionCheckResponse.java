package com.jp.kinto.app.bff.model.versionCheck;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class VersionCheckResponse {
  private String latestVersion;
  private Boolean upgradeRequire;
  private String latestTermVersion;
}
