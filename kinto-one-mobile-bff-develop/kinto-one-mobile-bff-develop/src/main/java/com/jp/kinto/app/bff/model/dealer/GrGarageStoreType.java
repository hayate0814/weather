package com.jp.kinto.app.bff.model.dealer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GrGarageStoreType {
  INDEPENDENT("INDEPENDENT", "/assets/img/icon/store_gr_independent.png"),
  ATTACHED("ATTACHED", "/assets/img/icon/store_gr_annex.png"),
  ADJACENT("ADJACENT", "/assets/img/icon/store_gr_adjacent.png"),
  SATELLITE("SATELLITE", "");

  private final String code;
  private final String iconUrl;

  public static String getIconUrlByCode(String code) {
    for (GrGarageStoreType type : GrGarageStoreType.values()) {
      if (type.getCode().equals(code)) {
        return type.getIconUrl();
      }
    }
    return "";
  }
}
