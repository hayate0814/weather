package com.jp.kinto.app.bff.model;

import static com.jp.kinto.app.bff.core.constant.Constant.APP_VERSION_PREFIX;

import com.jp.kinto.app.bff.core.constant.Constant;
import com.jp.kinto.app.bff.core.constant.Constant.DeviceKindSv;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.val;

@Data
@Builder(toBuilder = true)
public class AppVersion {
  private DeviceKindSv deviceKindSv;
  private String version;

  public static Optional<AppVersion> fromString(String versionText) {
    if (versionText == null || versionText.length() <= APP_VERSION_PREFIX.length()) {
      return Optional.empty();
    }

    String rs = versionText.substring(APP_VERSION_PREFIX.length());

    val verArr = versionText.split(":");
    if (verArr.length != 3) {
      return Optional.empty();
    }

    var deviceKindSv = Constant.DeviceKindSv.fromType(verArr[1]);
    if (deviceKindSv == null) {
      return Optional.empty();
    }
    return Optional.of(AppVersion.builder().deviceKindSv(deviceKindSv).version(rs).build());
  }
}
